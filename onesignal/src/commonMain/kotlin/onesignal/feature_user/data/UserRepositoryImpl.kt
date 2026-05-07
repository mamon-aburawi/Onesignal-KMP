package onesignal.feature_user.data

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import onesignal.core.config.OneSignalConfig
import onesignal.core.config.OneSignalConstants
import onesignal.core.getOneSignalId
import onesignal.core.getSubscriptionId
import onesignal.feature_email.addEmailToOneSignal
import onesignal.feature_email.removeEmailFromOneSignal
import onesignal.feature_push.listenForNotificationClicks
import onesignal.feature_push.requestNotificationPermission
import onesignal.feature_user.domain.UserRepository
import onesignal.feature_user.domain.model.OneSignalUser
import onesignal.feature_user.domain.model.UserProperties
import onesignal.feature_user.domain.model.UserSubscription
import onesignal.feature_user.loginOneSignalUser
import onesignal.feature_user.logoutOneSignalUser


internal class UserRepositoryImpl(
    private val config: OneSignalConfig,
    private val httpClient: HttpClient,
): UserRepository {

    private val userEndPoint = "${OneSignalConstants.BASE_URL}/apps/${config.appId}/users"


    override suspend fun getUserSubscriptionId(): String? = getSubscriptionId()
    override suspend fun getUserOneSignalId(): String? = getOneSignalId()

    override suspend fun createUser(
        externalId: String,
        userProperties: UserProperties?,
        userSubscriptions: List<UserSubscription>?
    ) {
        val payload = OneSignalUser(
            properties = userProperties,
            identity = mapOf("external_id" to externalId),
            subscriptions = userSubscriptions
        )

        try {
            val response = httpClient.post(urlString = userEndPoint) {
                header(HttpHeaders.Authorization, "Basic ${config.apiKey}")
                contentType(ContentType.Application.Json)
                setBody(payload)
            }

            if (!response.status.isSuccess()) {
                println("Failed to create user (${response.status.value}): ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            println("Ktor Network Error: ${e.message}")
        }
    }

    override suspend fun login(externalId: String) = loginOneSignalUser(externalId)

    override suspend fun addEmail(email: String) {
        addEmailToOneSignal(email)
    }

    override suspend fun removeEmail(email: String) {
        removeEmailFromOneSignal(email)
    }

    override suspend fun logout() = logoutOneSignalUser()

    override fun setNotificationClickListener(onClick: (title: String, body: String) -> Unit) {
        listenForNotificationClicks(onClick)
    }

    override suspend fun requestPermission() {
        requestNotificationPermission()
    }

}