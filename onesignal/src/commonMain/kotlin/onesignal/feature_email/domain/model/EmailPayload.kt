package onesignal.feature_email.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import onesignal.core.OneSignalChannelType

@Serializable
internal data class EmailPayload(
    @SerialName("app_id") var appId: String,

    @SerialName("target_channel")
    val targetChannel: OneSignalChannelType = OneSignalChannelType.EMAIL,


    // --- Email Specific Fields ---
    @SerialName("email_subject") val emailSubject: String? = null,
    @SerialName("email_body") val emailBody: String? = null,
    @SerialName("email_preheader") val emailPreheader: String? = null,
    @SerialName("email_from_name") val emailFromName: String? = null,
    @SerialName("email_from_address") val emailFromAddress: String? = null,
    @SerialName("email_reply_to_address") val emailReplyToAddress: String? = null,
    @SerialName("email_sender_domain") val emailSenderDomain: String? = null,
    @SerialName("email_to") val emailTo: List<String>? = null, // Array of specific email addresses
    @SerialName("template_id") val templateId: String? = null,
    val name: String? = null, // Internal tracking name for the OneSignal dashboard
    @SerialName("custom_data") val customData: Map<String, String>? = null,
    @SerialName("include_unsubscribed") val includeUnsubscribed: Boolean? = null,
    @SerialName("disable_email_click_tracking") val disableEmailClickTracking: Boolean? = null,

    // --- Targeting Fields ---
    @SerialName("include_subscription_ids") val subscriptionIds: List<String>? = null,
    @SerialName("included_segments") val segments: List<String>? = null,
    @SerialName("excluded_segments") val excludedSegments: List<String>? = null,
    @SerialName("include_aliases") val includeAliases: Map<String, List<String>>? = null,

    // --- Scheduling Fields ---
    @SerialName("send_after") val sendAfter: String? = null,
    @SerialName("delayed_option") val delayedOption: String? = null,
    @SerialName("delivery_time_of_day") val deliveryTimeOfDay: String? = null
)
