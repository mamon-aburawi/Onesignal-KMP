package onesignal.feature_push.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class OneSignalButton(
    val id: String,
    val text: String,
    val icon: String? = null, // Optional: Public URL to an image
    val url: String? = null   // Optional: URL to open when clicked
)

