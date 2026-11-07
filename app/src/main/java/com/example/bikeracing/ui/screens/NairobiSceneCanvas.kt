package com.example.bikeracing.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import com.example.bikeracing.domain.model.Opponent
import kotlin.math.abs

/**
 * Top-down vertical scrolling racing scene — Nairobi City theme.
 *
 * Matches the classic Road Fighter / Traffic Rider style:
 *  - Camera looks straight DOWN the road from above
 *  - Road fills ~60% of screen width in the centre
 *  - Nairobi city environment (buildings, matatus, trees) on both sides
 *  - Player bike at bottom-centre viewed from above
 *  - Opponents scroll towards the player from the top
 *  - Dashed yellow centre lines and white edge lines scroll downward
 */
@Composable
fun NairobiSceneCanvas(
    speedKmh: Float,
    lateralPosition: Float,   // -1..1  (player's left/right position on road)
    leanAngle: Float,          // degrees lean
    isNitroActive: Boolean,
    distanceTravelledKm: Float,
    opponents: List<Opponent>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Scroll offset drives all animated elements (road lines, buildings, etc.)
        val scrollPx = (distanceTravelledKm * 1000f * 1.5f) % h

        // Road occupies centre 60% of screen width
        val roadLeft  = w * 0.20f
        val roadRight = w * 0.80f
        val roadW     = roadRight - roadLeft

        // Player X position within the road
        val playerX = w * 0.5f + lateralPosition * (roadW * 0.38f)

        // ── Draw layers back to front ──
        drawNairobiSidewalk(w, h, roadLeft, roadRight)
        drawNairobiCityEnvironment(w, h, roadLeft, roadRight, scrollPx)
        drawRoadSurface(h, roadLeft, roadRight)
        drawRoadMarkings(w, h, roadLeft, roadRight, scrollPx)

        // Traffic opponents — draw farthest first (smallest, near top)
        opponents
            .filter { it.isActive }
            .sortedBy { it.position }   // smallest position = furthest ahead in top-down
            .forEach { opp ->
                drawTopDownOpponentBike(opp, h, roadLeft, roadRight, distanceTravelledKm * 1000f)
            }

        // Speed effect
        if (speedKmh > 80f) drawSpeedLines(w, h, roadLeft, roadRight, speedKmh)
        if (isNitroActive)  drawNitroTrail(playerX, h * 0.80f, h)

        // Player bike always last (on top)
        drawTopDownPlayerBike(
            x          = playerX,
            y          = h * 0.78f,
            leanAngle  = leanAngle,
            isNitro    = isNitroActive,
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// SIDEWALK / KERB AREA (the yellow sandy sides in the reference image)
// ──────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawNairobiSidewalk(
    w: Float, h: Float, roadLeft: Float, roadRight: Float,
) {
    // Left grass/sidewalk — sandy yellow like Nairobi roadsides
    drawRect(
        brush = Brush.horizontalGradient(
            listOf(Color(0xFFD4A843), Color(0xFFBF9030)),
            startX = 0f, endX = roadLeft,
        ),
        topLeft = Offset(0f, 0f),
        size    = Size(roadLeft, h),
    )

    // Right grass/sidewalk
    drawRect(
        brush = Brush.horizontalGradient(
            listOf(Color(0xFFBF9030), Color(0xFFD4A843)),
            startX = roadRight, endX = w,
        ),
        topLeft = Offset(roadRight, 0f),
        size    = Size(w - roadRight, h),
    )

    // Thin kerb lines at road edges
    drawLine(
        color       = Color(0xFFFFFFFF),
        start       = Offset(roadLeft, 0f),
        end         = Offset(roadLeft, h),
        strokeWidth = 4f,
    )
    drawLine(
        color       = Color(0xFFFFFFFF),
        start       = Offset(roadRight, 0f),
        end         = Offset(roadRight, h),
        strokeWidth = 4f,
    )
}

// ──────────────────────────────────────────────────────────────────────────────
// NAIROBI CITY ENVIRONMENT — buildings / trees / matatus on the sides
// ──────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawNairobiCityEnvironment(
    w: Float, h: Float, roadLeft: Float, roadRight: Float, scrollPx: Float,
) {
    val sideW = roadLeft               // width of each side strip
    val buildingSpacing = h * 0.28f    // vertical gap between building groups

    // Two columns: left side and right side
    for (side in 0..1) {
        val isLeft = side == 0
        val stripLeft  = if (isLeft) 0f else roadRight
        val stripRight = if (isLeft) roadLeft else w

        // 6 building slots that scroll
        for (i in 0..6) {
            val baseY   = (i * buildingSpacing - scrollPx % (buildingSpacing * 6)) % (h + buildingSpacing * 2) - buildingSpacing
            val seed    = i * 37 + side * 13
            val bldW    = sideW * (0.55f + (seed % 4) * 0.1f)
            val bldH    = h * (0.10f + (seed % 5) * 0.03f)

            val bx      = if (isLeft) stripLeft + (seed % 3) * 4f else roadRight + (seed % 3) * 2f
            val actualW = if (isLeft) minOf(bldW, roadLeft - bx) else minOf(bldW, w - bx)

            if (actualW <= 0f) continue

            // Building body — mix of Nairobi typical colours (cream, terracotta, grey)
            val buildingColors = listOf(
                Color(0xFFD4C5A0), Color(0xFFB8A87A), Color(0xFFC8B896),
                Color(0xFFAA9966), Color(0xFFD4B896), Color(0xFFBCAA88),
            )
            val roofColors = listOf(
                Color(0xFF884422), Color(0xFF996633), Color(0xFF774411),
                Color(0xFF555566), Color(0xFF446644), Color(0xFF885544),
            )
            val bColor = buildingColors[seed % buildingColors.size]
            val rColor = roofColors[seed % roofColors.size]

            drawRect(
                color   = bColor,
                topLeft = Offset(bx, baseY),
                size    = Size(actualW, bldH),
            )
            // Roof band
            drawRect(
                color   = rColor,
                topLeft = Offset(bx, baseY),
                size    = Size(actualW, bldH * 0.12f),
            )
            // Windows
            val winCols = ((actualW / 16f).toInt()).coerceIn(1, 6)
            val winRows = ((bldH / 20f).toInt()).coerceIn(1, 4)
            for (wc in 0 until winCols) {
                for (wr in 0 until winRows) {
                    val wx = bx + wc * (actualW / winCols) + 3f
                    val wy = baseY + bldH * 0.2f + wr * (bldH * 0.65f / winRows)
                    val lit = (wc + wr + seed) % 3 != 0
                    drawRect(
                        color   = if (lit) Color(0xFFFFDD88).copy(alpha = 0.85f)
                                  else Color(0xFF333322).copy(alpha = 0.7f),
                        topLeft = Offset(wx, wy),
                        size    = Size(7f, 10f),
                    )
                }
            }

            // Trees between buildings (every other slot on the inner edge)
            if (seed % 2 == 0) {
                val treeX = if (isLeft) roadLeft - 14f else roadRight + 6f
                val treeY = baseY + bldH * 0.5f
                // Trunk
                drawRect(
                    color   = Color(0xFF664422),
                    topLeft = Offset(treeX + 3f, treeY),
                    size    = Size(7f, 22f),
                )
                // Canopy
                drawCircle(
                    color  = Color(0xFF2D6A2D),
                    radius = 13f,
                    center = Offset(treeX + 6.5f, treeY),
                )
                drawCircle(
                    color  = Color(0xFF3D8A3D),
                    radius = 9f,
                    center = Offset(treeX + 6.5f, treeY - 4f),
                )
            }

            // Matatu parked on left side occasionally
            if (seed % 4 == 0 && isLeft) {
                drawParkedMatatu(
                    x = bx + actualW * 0.1f,
                    y = baseY + bldH + 4f,
                    w = actualW * 0.75f,
                )
            }
        }
    }
}

/** Parked minibus (matatu) — iconic Nairobi public transport */
private fun DrawScope.drawParkedMatatu(x: Float, y: Float, w: Float) {
    val h2 = w * 0.45f
    // Body
    drawRect(Color(0xFFCC2222), topLeft = Offset(x, y), size = Size(w, h2))
    // Yellow stripe
    drawRect(Color(0xFFFFCC00), topLeft = Offset(x, y + h2 * 0.4f), size = Size(w, h2 * 0.2f))
    // Windows
    drawRect(Color(0xFF88CCFF).copy(alpha = 0.7f), topLeft = Offset(x + w * 0.1f, y + h2 * 0.1f), size = Size(w * 0.35f, h2 * 0.28f))
    drawRect(Color(0xFF88CCFF).copy(alpha = 0.7f), topLeft = Offset(x + w * 0.55f, y + h2 * 0.1f), size = Size(w * 0.35f, h2 * 0.28f))
    // Wheels
    drawCircle(Color(0xFF111111), radius = w * 0.10f, center = Offset(x + w * 0.22f, y + h2))
    drawCircle(Color(0xFF111111), radius = w * 0.10f, center = Offset(x + w * 0.78f, y + h2))
}

// ──────────────────────────────────────────────────────────────────────────────
// ROAD SURFACE
// ──────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawRoadSurface(h: Float, roadLeft: Float, roadRight: Float) {
    val roadW = roadRight - roadLeft
    // Tarmac — slightly purple-grey like Nairobi asphalt
    drawRect(
        color   = Color(0xFF8A7A8A),
        topLeft = Offset(roadLeft, 0f),
        size    = Size(roadW, h),
    )
}

// ──────────────────────────────────────────────────────────────────────────────
// ROAD MARKINGS — scrolling centre dashes + lane dividers
// ──────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawRoadMarkings(
    w: Float, h: Float, roadLeft: Float, roadRight: Float, scrollPx: Float,
) {
    val roadW       = roadRight - roadLeft
    val centerX     = roadLeft + roadW * 0.5f
    val dashH       = 55f
    val dashGap     = 40f
    val dashCycle   = dashH + dashGap
    val startOffset = scrollPx % dashCycle

    // ── Yellow centre dashes ──
    var y = -dashCycle + startOffset
    while (y < h + dashCycle) {
        if (y + dashH > 0f && y < h) {
            drawRect(
                color   = Color(0xFFFFDD00),
                topLeft = Offset(centerX - 4f, y),
                size    = Size(8f, dashH),
            )
        }
        y += dashCycle
    }

    // ── White lane dividers (left lane and right lane inner lines) ──
    val laneX1 = roadLeft + roadW * 0.333f
    val laneX2 = roadLeft + roadW * 0.667f

    for (lx in listOf(laneX1, laneX2)) {
        var ly = -dashCycle + startOffset
        while (ly < h + dashCycle) {
            if (ly + dashH > 0f && ly < h) {
                drawRect(
                    color   = Color(0xCCFFFFFF),
                    topLeft = Offset(lx - 2.5f, ly),
                    size    = Size(5f, dashH * 0.7f),
                )
            }
            ly += dashCycle
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// OPPONENT / TRAFFIC BIKES — top-down view
// ──────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawTopDownOpponentBike(
    opp: Opponent,
    h: Float,
    roadLeft: Float,
    roadRight: Float,
    playerDistanceM: Float,
) {
    // Relative distance: positive means opponent is ahead (further up on screen)
    val relDist = opp.position - playerDistanceM

    // Only render bikes within ±600 m
    if (relDist < -60f || relDist > 600f) return

    val roadW = roadRight - roadLeft

    // Map: relDist=600 → top of screen (y≈0), relDist=0 → player position (y≈h*0.78)
    // relDist=-60 → below player (y > h*0.78)
    val yFrac = 1f - (relDist / 600f).coerceIn(0f, 1f)
    val y     = h * 0.05f + (h * 0.73f) * yFrac   // distributes from top to player row

    // Scale by distance — far bikes are smaller
    val scale = (0.3f + yFrac * 0.7f).coerceIn(0.25f, 1f)

    // Lane to X position: lane -1=left, 0=centre, 1=right
    val laneX = roadLeft + roadW * (0.5f + opp.lane.toFloat() * 0.28f)

    val bikeColors = listOf(
        Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF43A047),
        Color(0xFFFFD600), Color(0xFF8E24AA), Color(0xFFFF6D00),
    )
    val bikeColor = bikeColors[abs(opp.id.hashCode()) % bikeColors.size]

    drawTopDownBikeSprite(laneX, y, scale, bikeColor, isPlayer = false, leanDeg = 0f)
}

// ──────────────────────────────────────────────────────────────────────────────
// PLAYER BIKE — top-down view, bottom-centre
// ──────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawTopDownPlayerBike(
    x: Float, y: Float, leanAngle: Float, isNitro: Boolean,
) {
    drawTopDownBikeSprite(x, y, scale = 1.0f, color = Color(0xFF1565C0), isPlayer = true, leanDeg = leanAngle)
}

/**
 * Top-down motorcycle sprite — the rider is seen from directly above.
 *
 * Shape:
 *   ┌───────────────────────────────┐
 *   │  front wheel (ellipse)        │   ← top of sprite
 *   │  fairing body (rounded rect)  │
 *   │  rider helmet (circle)        │
 *   │  rear wheel (ellipse)         │   ← bottom of sprite
 *   └───────────────────────────────┘
 */
private fun DrawScope.drawTopDownBikeSprite(
    x: Float,
    y: Float,
    scale: Float,
    color: Color,
    isPlayer: Boolean,
    leanDeg: Float,
) {
    val bw = 22f * scale   // bike width
    val bh = 50f * scale   // bike height (front to back)
    val ww = bw * 0.55f    // wheel width
    val wh = bh * 0.22f    // wheel height

    withTransform({
        translate(left = x, top = y)
        rotate(degrees = leanDeg * 0.4f)
    }) {
        // ── Shadow ──
        drawOval(
            color   = Color.Black.copy(alpha = 0.25f),
            topLeft = Offset(-bw * 0.7f, -bh * 0.55f),
            size    = Size(bw * 1.4f, bh * 1.1f),
        )

        // ── Rear wheel ──
        drawOval(
            color   = Color(0xFF111111),
            topLeft = Offset(-ww / 2, bh * 0.22f),
            size    = Size(ww, wh),
        )
        // Rear tyre shine
        drawOval(
            color   = Color(0xFF444444),
            topLeft = Offset(-ww * 0.3f, bh * 0.24f),
            size    = Size(ww * 0.6f, wh * 0.6f),
        )

        // ── Body / fairing — top-down silhouette ──
        val bodyPath = Path().apply {
            // Nose (front pointed)
            moveTo(0f, -bh * 0.50f)
            cubicTo(-bw * 0.5f, -bh * 0.38f, -bw * 0.52f, bh * 0.20f, -bw * 0.38f, bh * 0.30f)
            lineTo(bw * 0.38f, bh * 0.30f)
            cubicTo(bw * 0.52f, bh * 0.20f, bw * 0.5f, -bh * 0.38f, 0f, -bh * 0.50f)
            close()
        }
        drawPath(bodyPath, color = color)

        // Fairing highlight stripe
        drawPath(
            path = Path().apply {
                moveTo(0f, -bh * 0.48f)
                cubicTo(-bw * 0.12f, -bh * 0.30f, -bw * 0.10f, bh * 0.10f, 0f, bh * 0.18f)
                cubicTo(bw * 0.10f, bh * 0.10f, bw * 0.12f, -bh * 0.30f, 0f, -bh * 0.48f)
                close()
            },
            color = Color.White.copy(alpha = 0.18f),
        )

        // ── Engine / tank detail ──
        drawOval(
            color   = color.copy(alpha = 0.7f),
            topLeft = Offset(-bw * 0.25f, -bh * 0.08f),
            size    = Size(bw * 0.50f, bh * 0.22f),
        )

        // ── Rider helmet (top-down = just a circle/oval) ──
        val helmetColor = if (isPlayer) Color(0xFFFFCC00) else Color(0xFF222222)
        drawCircle(
            color  = helmetColor,
            radius = bw * 0.32f,
            center = Offset(0f, -bh * 0.10f),
        )
        // Visor
        drawArc(
            color      = Color(0xFF88CCFF).copy(alpha = if (isPlayer) 0.8f else 0.4f),
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter  = false,
            topLeft    = Offset(-bw * 0.25f, -bh * 0.22f),
            size       = Size(bw * 0.50f, bw * 0.50f),
        )

        // ── Front wheel ──
        drawOval(
            color   = Color(0xFF111111),
            topLeft = Offset(-ww / 2, -bh * 0.50f - wh),
            size    = Size(ww, wh),
        )
        drawOval(
            color   = Color(0xFF444444),
            topLeft = Offset(-ww * 0.3f, -bh * 0.50f - wh * 0.75f),
            size    = Size(ww * 0.6f, wh * 0.5f),
        )

        // ── Headlight (player only) ──
        if (isPlayer) {
            drawOval(
                color   = Color(0xFFFFFFCC),
                topLeft = Offset(-bw * 0.12f, -bh * 0.52f - wh - 4f),
                size    = Size(bw * 0.24f, 8f),
            )
        } else {
            // Tail light visible from behind
            drawOval(
                color   = Color(0xFFFF2200).copy(alpha = 0.9f),
                topLeft = Offset(-bw * 0.12f, bh * 0.30f),
                size    = Size(bw * 0.24f, 7f),
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// SPEED LINES
// ──────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawSpeedLines(
    w: Float, h: Float, roadLeft: Float, roadRight: Float, speedKmh: Float,
) {
    val alpha = ((speedKmh - 80f) / 220f).coerceIn(0f, 0.5f)
    val lineCount = 14

    for (i in 0 until lineCount) {
        val xFrac = (i.toFloat() / lineCount)
        // Lines on both side strips only (not over road)
        val leftX  = roadLeft * xFrac
        val rightX = roadRight + (w - roadRight) * xFrac
        val lineLen = h * 0.18f * alpha

        drawLine(
            color       = Color.White.copy(alpha = alpha),
            start       = Offset(leftX, h * 0.5f),
            end         = Offset(leftX, h * 0.5f + lineLen),
            strokeWidth = 2f,
            cap         = StrokeCap.Round,
        )
        drawLine(
            color       = Color.White.copy(alpha = alpha),
            start       = Offset(rightX, h * 0.5f),
            end         = Offset(rightX, h * 0.5f + lineLen),
            strokeWidth = 2f,
            cap         = StrokeCap.Round,
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// NITRO TRAIL
// ──────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawNitroTrail(x: Float, y: Float, h: Float) {
    // Blue flame trail downward from exhaust
    drawPath(
        path = Path().apply {
            moveTo(x - 8f, y)
            cubicTo(x - 14f, y + h * 0.08f, x, y + h * 0.06f, x, y + h * 0.12f)
            cubicTo(x, y + h * 0.06f, x + 14f, y + h * 0.08f, x + 8f, y)
            close()
        },
        brush = Brush.verticalGradient(
            listOf(Color(0xFF00CCFF), Color(0x000044FF)),
            startY = y,
            endY   = y + h * 0.12f,
        ),
    )
    // Inner white core
    drawPath(
        path = Path().apply {
            moveTo(x - 4f, y)
            cubicTo(x - 5f, y + h * 0.05f, x, y + h * 0.04f, x, y + h * 0.07f)
            cubicTo(x, y + h * 0.04f, x + 5f, y + h * 0.05f, x + 4f, y)
            close()
        },
        color = Color(0xBBFFFFFF),
    )
}
