package com.it2161.s243168t.movieviewer.utils

import java.text.NumberFormat
import java.util.Locale

fun formatRevenue(revenue: Long): String {
    return if (revenue > 0) {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        formatter.format(revenue)
    } else {
        "N/A"
    }
}

fun formatRuntime(runtimeMinutes: Int): String {
    return if (runtimeMinutes > 0) {
        val hours = runtimeMinutes / 60
        val minutes = runtimeMinutes % 60
        when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
    } else {
        "N/A"
    }
}
