package com.example.bikeracing.domain.model

data class Motorcycle(
    val id: String,
    val name: String,
    val category: MotorcycleCategory,
    val baseTopSpeed: Float,      // km/h
    val baseAcceleration: Float,  // 0-100 rating
    val baseHandling: Float,      // 0-100 rating
    val baseBraking: Float,       // 0-100 rating
    val baseNitroCapacity: Float, // 0-100 rating
    val price: Int,               // coins
    val gemPrice: Int = 0,
    val unlockLevel: Int = 1,
    val colorOptions: List<MotorcycleColor> = defaultColors(),
    val description: String = "",
    val modelAsset: String = "",   // asset path for 3D model
) {
    companion object {
        fun defaultColors() = listOf(
            MotorcycleColor("Red", 0xFFE53935),
            MotorcycleColor("Blue", 0xFF1E88E5),
            MotorcycleColor("Black", 0xFF212121),
            MotorcycleColor("White", 0xFFF5F5F5),
            MotorcycleColor("Yellow", 0xFFFDD835),
            MotorcycleColor("Green", 0xFF43A047),
            MotorcycleColor("Orange", 0xFFFB8C00),
            MotorcycleColor("Purple", 0xFF8E24AA),
        )
    }
}

enum class MotorcycleCategory {
    STREET, SPORT, SUPERBIKE, RACING, PREMIUM
}

data class MotorcycleColor(val name: String, val colorHex: Long)

data class MotorcycleUpgrades(
    val motorcycleId: String,
    val engineLevel: Int = 0,        // 0-5
    val accelerationLevel: Int = 0,
    val brakesLevel: Int = 0,
    val handlingLevel: Int = 0,
    val nitroLevel: Int = 0,
    val suspensionLevel: Int = 0,
    val selectedColorIndex: Int = 0,
) {
    val maxLevel: Int get() = 5
    fun getUpgradedStat(base: Float, level: Int): Float = base + (level * base * 0.1f)
}
