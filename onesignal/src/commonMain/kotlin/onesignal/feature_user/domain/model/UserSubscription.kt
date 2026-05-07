package onesignal.feature_user.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserSubscription(
    val type: String, // E.g., "Email", "iOS Push", "Android Push"
    val token: String? = null,
    val enabled: Boolean? = true,
    @SerialName("notification_types") val notificationTypes: Int? = null,
    @SerialName("session_time") val sessionTime: Long? = null,
    @SerialName("session_count") val sessionCount: Int? = null,
    @SerialName("app_version") val appVersion: String? = null,
    @SerialName("device_model") val deviceModel: String? = null,
    @SerialName("device_os") val deviceOs: String? = null,
    @SerialName("test_type") val testType: Int? = null,
    val sdk: String? = null,
    @SerialName("web_auth") val webAuth: String? = null,
    @SerialName("web_p256") val webP256: String? = null
)
