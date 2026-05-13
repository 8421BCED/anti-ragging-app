package com.joel.antirag

import kotlinx.serialization.Serializable

@Serializable
data class Complaint(
    val id: String? = null,
    val name: String? = null,
    val roll_no: String? = null,
    val year: String? = null,
    val section: String? = null,
    val department: String? = null,
    val problem: String,
    val image_url: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val is_anonymous: Boolean = false,
    val status: String = "pending",
    val created_at: String? = null
)

@Serializable
data class SosAlert(
    val id: String? = null,
    val latitude: Double,
    val longitude: Double,
    val message: String = "SOS Alert Triggered!",
    val created_at: String? = null
)
