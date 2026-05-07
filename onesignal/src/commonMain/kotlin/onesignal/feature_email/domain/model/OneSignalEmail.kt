package onesignal.feature_email.domain.model

import onesignal.feature_scheduler.domain.model.OneSignalScheduleConfig


data class OneSignalEmail(
    val subject: String,
    val body: String, // The HTML or plain text body of the email
    val preheader: String? = null, // The short preview text shown in inboxes
    val fromName: String? = null,
    val fromAddress: String? = null,
    val replyToAddress: String? = null,
    val scheduledTime: OneSignalScheduleConfig? = null
)