package onesignal.feature_push.domain.model

import onesignal.feature_scheduler.domain.model.OneSignalScheduleConfig


data class OneSignalNotification(
    val title: String,
    val message: String,
    val languageCode: String = "en",
    val largeImage: String? = null,
    val actionUrl: String? = null,
    val smallIcon: String? = null,
    val largeIcon: String? = null,
    val buttons: List<OneSignalButton>? = null,
    val scheduledTime: OneSignalScheduleConfig? = null
)
