package com.fathurrohman.piquizapps

import android.health.connect.datatypes.units.Percentage

data class QuizResult(
    val documentId: String,
    val title: String,
    val score: Int,
    val timestamp: Long
)