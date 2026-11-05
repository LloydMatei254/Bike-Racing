package com.example.bikeracing.utils

import kotlin.math.roundToInt

fun Float.toKmhString() = "${this.roundToInt()} km/h"
fun Float.formatTime(): String {
    val mins = (this / 60).toInt()
    val secs = (this % 60).toInt()
    return "%02d:%02d".format(mins, secs)
}

fun Int.formatCoins(): String = when {
    this >= 1_000_000 -> "${this / 1_000_000}M"
    this >= 1_000 -> "${this / 1_000}K"
    else -> "$this"
}
