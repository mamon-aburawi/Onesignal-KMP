package onesignal.feature_scheduler.domain.model

data class OneSignalScheduleConfig(
    val sendAfter: String,
    val delayedOption: String? = null,
    val deliveryTimeOfDay: String? = null
)