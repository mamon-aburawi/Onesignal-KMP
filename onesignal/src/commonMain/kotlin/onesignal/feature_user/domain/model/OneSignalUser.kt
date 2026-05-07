package onesignal.feature_user.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class OneSignalUser(
    val properties: UserProperties? = null,
    val identity: Map<String, String>,
    val subscriptions: List<UserSubscription>? = null
)




