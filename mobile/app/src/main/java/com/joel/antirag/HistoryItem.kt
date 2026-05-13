package com.joel.antirag

import kotlinx.serialization.Serializable

@Serializable
data class HistoryItem(
    val type: String, // "COMPLAINT" or "SOS"
    val title: String,
    val subtitle: String,
    val status: String,
    val timestamp: String?
)
