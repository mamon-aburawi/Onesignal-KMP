package onesignal.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import onesignal.core.config.OneSignalConfig
import onesignal.feature_email.data.PushEmailRepoImpl
import onesignal.feature_email.domain.PushEmailRepository
import onesignal.feature_push.domain.PushNotificationRepository
import onesignal.feature_push.data.PushNotificationRepositoryImpl
import onesignal.feature_user.domain.UserRepository
import onesignal.feature_user.data.UserRepositoryImpl
import onesignal.feature_push.domain.model.OneSignalNotification
import onesignal.feature_user.domain.model.UserProperties
import onesignal.feature_user.domain.model.UserSubscription


class OneSignalClient(
    private val appId: String,
    private val apiKey: String
): AutoCloseable {
    private val config = OneSignalConfig(appId, apiKey)

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                encodeDefaults = true
                explicitNulls = false
            })
        }
    }

    private val pushNotificationOneSignalRepository: PushNotificationRepository by lazy {
        PushNotificationRepositoryImpl(config, httpClient)
    }
    private val userOneSignalRepository: UserRepository by lazy {
        UserRepositoryImpl(config, httpClient)
    }

    private val pushEmailOneSignalRepository: PushEmailRepository by lazy {
        PushEmailRepoImpl(config, httpClient)
    }

    /**  push notification targeting a specific collection
     * of user aliases defined by their External IDs.
     */
    suspend fun sendNotification(params: OneSignalNotification, externalIds: List<String>) =
        pushNotificationOneSignalRepository.sendNotification(params, externalIds)

    /** * Delivers a push notification to a single unique user
     * identified by their specific External ID alias.
     */
    suspend fun sendNotification(params: OneSignalNotification, externalId: String) =
        pushNotificationOneSignalRepository.sendNotification(params, externalId)

    /** * Sends a notification payload to multiple devices using
     * a provided list of specific OneSignal subscription identifiers.
     */
    suspend fun sendNotification(subscriptionIds: List<String>, params: OneSignalNotification) =
        pushNotificationOneSignalRepository.sendNotification(subscriptionIds, params)

    /** * Sends a notification payload to a single specific device
     * associated with the provided OneSignal subscription ID.
     */
    suspend fun sendNotification(subscriptionId: String, params: OneSignalNotification) =
        pushNotificationOneSignalRepository.sendNotification(subscriptionId, params)

    /** * Executes a global broadcast notification that targets every
     * currently subscribed user login within the Onesignal.
     */
    suspend fun sendNotificationToAll(params: OneSignalNotification) =
        pushNotificationOneSignalRepository.sendNotificationToAll(params)



//    suspend fun sendEmail(email: OneSignalEmail, targetEmail: String) =
//        pushEmailOneSignalRepository.sendEmail(email = email, targetEmail = targetEmail)
//
//
//    suspend fun sendEmail(email: OneSignalEmail, targetEmails: List<String>) =
//        pushEmailOneSignalRepository.sendEmail(email = email, targetEmails = targetEmails)
//
//
//    suspend fun sendEmailToAll(email: OneSignalEmail) =
//        pushEmailOneSignalRepository.sendEmailToAll(email = email)



    /**
     *  Sets a callback to be triggered when the user taps on a notification.
     */
    fun setNotificationClickListener(onClick: (title: String, body: String) -> Unit) {
        userOneSignalRepository.setNotificationClickListener(onClick)
    }

    /** * Triggers the native operating system prompt to request
     * explicit push notification permissions from the user.
     */
    suspend fun requestPermission() {
        userOneSignalRepository.requestPermission()
    }

    /** * Fetches the current device's unique subscription identifier
     * used by OneSignal for targeted messaging
     */
    suspend fun getSubscriptionId() = userOneSignalRepository.getUserSubscriptionId()

    suspend fun getOneSignalId() = userOneSignalRepository.getUserOneSignalId()

    /**
     *  Provisions a new user identity in the OneSignal system
     */
    suspend fun addUser(
        externalId: String,
        userSubscriptions: List<UserSubscription>,
        userProperties: UserProperties? = null
    ) {
        userOneSignalRepository.createUser(
            externalId = externalId,
            userProperties = userProperties,
            userSubscriptions = userSubscriptions
        )
    }

    /** * Establishes an active user session by authenticating
     * the device with the provided External ID.
     */
    suspend fun login(externalId: String) {
        userOneSignalRepository.login(externalId)
    }

    /** * Terminates the active user session and detaches the
     * current device from the associated External ID.
     */
    suspend fun logout() {
        userOneSignalRepository.logout()
    }


    suspend fun addEmail(email: String){
        userOneSignalRepository.addEmail(email)
    }

    suspend fun removeEmail(email: String){
        userOneSignalRepository.removeEmail(email)
    }


    override fun close() {
        httpClient.close()
    }


}

