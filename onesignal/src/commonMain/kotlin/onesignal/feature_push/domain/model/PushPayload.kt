package onesignal.feature_push.domain.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import onesignal.core.OneSignalChannelType


@OptIn(ExperimentalSerializationApi::class)
@Serializable
internal data class PushPayload(
    @SerialName("app_id") var appId: String,
    val contents: Map<String, String>,
    val headings: Map<String, String>? = null,



    @SerialName("fcm_launch_url")
    val fcmLaunchUrl: String? = null,


    @SerialName("chrome_web_icon")
    val webIcon: String? = null, // NEW: This replaces the default Google Chrome logo


    @SerialName("large_icon")
    val largeIcon: String? = null, // NEW: This puts the icon on the right side on Android


    @SerialName("big_picture")
    val bigPicture: String? = null,


    @SerialName("chrome_web_image")
    val webImage: String? = null,


    @SerialName("ios_attachments")
    val iosAttachments: Map<String, String>? = null,


    @SerialName("small_icon")
    val smallIcon: String? = null,

    // --- Standard Targeting Fields ---

    @SerialName("include_subscription_ids")
    val subscriptionIds: List<String>? = null,


    @SerialName("included_segments")
    val segments: List<String>? = null,


    @SerialName("target_channel")
    val targetChannel: OneSignalChannelType = OneSignalChannelType.PUSH,


    @SerialName("include_aliases")
    val includeAliases: Map<String, List<String>>? = null,


    val data: Map<String, String>? = null,


    // Add the Action Buttons here
    val buttons: List<OneSignalButton>? = null,

    @SerialName("web_buttons")
    val webButtons: List<OneSignalButton>? = null,

    @SerialName("app_url")
    val appUrl: String? = null,

    @SerialName("web_url")
    val webUrl: String? = null,

    @SerialName("send_after")
    val sendAfter: String? = null,

// For Timezone or "Intelligent" scheduling
    @SerialName("delayed_option")
    val delayedOption: String? = null,

// Format must be "9:00AM" or "10:30PM". Required if delayedOption = "timezone"
    @SerialName("delivery_time_of_day")
    val deliveryTimeOfDay: String? = null

)












