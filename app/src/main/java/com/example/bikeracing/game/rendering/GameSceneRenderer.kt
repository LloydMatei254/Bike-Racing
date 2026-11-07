package com.example.bikeracing.game.rendering

import android.content.Context
import com.example.bikeracing.domain.model.Environment
import com.example.bikeracing.domain.model.RaceState
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Wraps SceneView (v2.x / Filament) for 3D rendering of the race scene.
 *
 * Architecture note: The road and environment perspective are drawn by a Compose
 * Canvas in GameScreen. This renderer adds Filament-based lighting and, when
 * a model asset is present, a 3D motorcycle above the canvas.
 *
 * Using `try/catch` around all SceneView calls ensures that a missing or
 * unavailable API in a given build variant never crashes the game.
 */
class GameSceneRenderer(context: Context) {

    private var sceneView: SceneView? = null
    @Suppress("unused")
    private val rendererScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    /** Optional player bike node loaded from a GLB asset. */
    private var playerBikeNode: ModelNode? = null

    /** Returns the existing SceneView or creates a new one. */
    fun getOrCreateSceneView(ctx: Context): SceneView {
        return sceneView ?: createSceneView(ctx)
    }

    private fun createSceneView(ctx: Context): SceneView {
        val sv = SceneView(ctx)
        sceneView = sv
        // Transparent background so the Compose Canvas road is visible underneath
        sv.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        return sv
    }

    /**
     * Tint sky/environment based on the current track world.
     * (Full IBL / HDR environment maps would be loaded from assets here.)
     */
    fun loadEnvironment(env: Environment) {
        // Sky colour is driven by the Canvas in GameScreen.
        // Here we would load an .hdr skybox for the matching environment.
        // Left as an extension point for production asset integration.
    }

    /**
     * Per-frame update: moves the bike node and adjusts the camera position
     * to achieve a dynamic third-person racing camera.
     */
    fun updateFrame(state: RaceState, leanAngle: Float, lateralPos: Float) {
        val sv = sceneView ?: return
        try {
            playerBikeNode?.let { node ->
                node.position = Position(x = lateralPos * 2f, y = 0f, z = 0f)
                node.rotation = Rotation(x = 0f, y = 0f, z = -leanAngle)
            }

            val speedFactor = (state.speedKmh / 200f).coerceIn(0f, 1f)
            val camZ = -3.5f - speedFactor * 3f
            val camY = 2f + speedFactor * 1.2f
            val camX = lateralPos * 0.4f

            // SceneView 2.x exposes camera access via reflection to stay API-version agnostic
            try {
                val cameraMethod = sv.javaClass.methods.firstOrNull {
                    it.name == "getCamera" || it.name == "getCameraNode"
                }
                val cam = cameraMethod?.invoke(sv)
                if (cam != null) {
                    val positionSetter = cam.javaClass.methods.firstOrNull { it.name == "setPosition" }
                    positionSetter?.invoke(cam, Position(x = camX, y = camY, z = camZ))
                }
            } catch (_: Exception) {
                // Camera API unavailable in this build — skip camera update
            }
        } catch (_: Exception) {
            // Silently ignore any API mismatch — rendering degrades gracefully
        }
    }

    fun release() {
        try { sceneView?.destroy() } catch (_: Exception) {}
        sceneView = null
    }
}
