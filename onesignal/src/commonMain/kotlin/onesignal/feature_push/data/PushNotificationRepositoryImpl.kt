package onesignal.feature_push.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
import onesignal.core.error.OneSignalException
import onesignal.core.network.OneSignalResponse
import onesignal.feature_push.domain.PushNotificationRepository
import onesignal.feature_push.domain.model.OneSignalNotification
import onesignal.feature_push.domain.model.PushPayload

class PushNotificationRepositoryImpl(
    private val config: OneSignalConfig,
    private val httpClient: HttpClient,
): PushNotificationRepository {

    private val notificationPushedEndPoint = "${OneSignalConstants.BASE_URL}/notifications?c=push"



    override suspend fun sendNotification(params: OneSignalNotification, externalIds: List<String>): Result<String> =
        postNotification(buildPayload(params, includeAliases = mapOf("external_id" to externalIds)))


    override suspend fun sendNotification(params: OneSignalNotification, externalId: String): Result<String> =
        sendNotification(params, listOf(externalId))

    override suspend fun sendNotification(subscriptionIds: List<String>, params: OneSignalNotification): Result<String> =
        postNotification(buildPayload(params, subscriptionIds = subscriptionIds))

    override suspend fun sendNotification(subscriptionId: String, params: OneSignalNotification): Result<String> =
        sendNotification(listOf(subscriptionId), params)


    override suspend fun sendNotificationToAll(params: OneSignalNotification): Result<String> =
        postNotification(buildPayload(params, segments = listOf("Total Subscriptions")))



    private fun buildPayload(
        params: OneSignalNotification,
        segments: List<String>? = null,
        includeAliases: Map<String, List<String>>? = null,
        subscriptionIds: List<String>? = null
    ): PushPayload {
        return PushPayload(
            appId = config.appId,
            headings = mapOf(params.languageCode to params.title),
            contents = mapOf(params.languageCode to params.message),
            segments = segments,
            subscriptionIds = subscriptionIds,
            includeAliases = includeAliases,
            smallIcon = params.smallIcon,
            largeIcon = params.largeImage,
            webIcon = params.largeIcon,
            bigPicture = params.largeImage,
            webImage = params.largeImage,
            iosAttachments = params.largeImage?.let { mapOf("id1" to it) },
            appUrl = params.actionUrl,
            webUrl = params.actionUrl,
            buttons = params.buttons,
            webButtons = params.buttons,
            sendAfter = params.scheduledTime?.sendAfter,
            delayedOption = params.scheduledTime?.delayedOption,
            deliveryTimeOfDay = params.scheduledTime?.deliveryTimeOfDay
        )
    }



    private suspend fun postNotification(payload: PushPayload): Result<String> {
        return try {
            val response = httpClient.post(urlString = notificationPushedEndPoint) {
                header(HttpHeaders.Authorization, "Basic ${config.apiKey}")
                contentType(ContentType.Application.Json)
                setBody(payload)
            }

            if (response.status.isSuccess()) {
                val result = response.body<OneSignalResponse>()
                if (!result.id.isNullOrBlank() && result.errors == null) {
                    Result.success(result.id)
                } else if (result.errors != null) {
                    Result.failure(OneSignalException.PayloadError(result.errors, result.id))
                } else {
                    Result.failure(OneSignalException.UnknownException("No notification ID returned from OneSignal."))
                }
            } else {
                val errorText = response.bodyAsText()
                Result.failure(OneSignalException.ApiRequestError(response.status.value, errorText))
            }
        } catch (e: Exception) {
            Result.failure(OneSignalException.NetworkException(e))
        }
    }







}