package onesignal.feature_user.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProperties(
    val tags: Map<String, String>? = null,
    val language: String? = null,
    @SerialName("timezone_id") val timezoneId: String? = null,
    val lat: Double? = null,
    val long: Double? = null,
    val country: String? = null,
    @SerialName("first_active") val firstActive: Long? = null,
    @SerialName("last_active") val lastActive: Long? = null,
    val ip: String? = null,
    @SerialName("test_user_name") val testUserName: String? = null
)
