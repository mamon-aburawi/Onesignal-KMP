package onesignal.feature_user.domain

import onesignal.feature_user.domain.model.UserProperties
import onesignal.feature_user.domain.model.UserSubscription

interface UserRepository {
    suspend fun getUserSubscriptionId(): String?

    suspend fun getUserOneSignalId(): String?

    suspend fun createUser(
        externalId: String,
        userProperties: UserProperties? = null,
        userSubscriptions: List<UserSubscription>? = null
    )

    suspend fun login(externalId: String)

    suspend fun addEmail(email: String)

    suspend fun removeEmail(email: String)


    suspend fun logout()

    fun setNotificationClickListener(onClick: (title: String, body: String) -> Unit)
    suspend fun requestPermission()



}