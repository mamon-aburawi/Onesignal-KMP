package onesignal.feature_email.data

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
import onesignal.feature_email.domain.PushEmailRepository
import onesignal.feature_email.domain.model.EmailPayload
import onesignal.feature_email.domain.model.OneSignalEmail

internal class PushEmailRepoImpl(
    private val config: OneSignalConfig,
    private val httpClient: HttpClient,
): PushEmailRepository {

    private val emailPushedEndPoint = "${OneSignalConstants.BASE_URL}/notifications?c=email"


    override suspend fun sendEmailToAll(email: OneSignalEmail): Result<String> {
        val payload = EmailPayload(
            appId = config.appId,
            emailSubject = email.subject,
            emailBody = email.body,
            emailPreheader = email.preheader,
            emailFromName = email.fromName,
            emailFromAddress = email.fromAddress,
            emailReplyToAddress = email.replyToAddress,

            // Targeting Everyone
            segments = listOf("Total Subscriptions"),

            // Scheduling
            sendAfter = email.scheduledTime?.sendAfter,
            delayedOption = email.scheduledTime?.delayedOption,
            deliveryTimeOfDay = email.scheduledTime?.deliveryTimeOfDay
        )

        return postEmailNotification(payload)
    }

    override suspend fun sendEmail(
        email: OneSignalEmail,
        targetEmail: String
    ): Result<String> {
        val payload = EmailPayload(
            appId = config.appId,
            emailSubject = email.subject,
            emailBody = email.body,
            emailPreheader = email.preheader,
            emailFromName = email.fromName,
            emailFromAddress = email.fromAddress,
            emailReplyToAddress = email.replyToAddress,
            segments = listOf(targetEmail),

            // Scheduling
            sendAfter = email.scheduledTime?.sendAfter,
            delayedOption = email.scheduledTime?.delayedOption,
            deliveryTimeOfDay = email.scheduledTime?.deliveryTimeOfDay
        )
        return postEmailNotification(payload)
    }

    override suspend fun sendEmail(
        email: OneSignalEmail,
        targetEmails: List<String>
    ): Result<String> {
        val payload = EmailPayload(
            appId = config.appId,
            emailSubject = email.subject,
            emailBody = email.body,
            emailPreheader = email.preheader,
            emailFromName = email.fromName,
            emailFromAddress = email.fromAddress,
            emailReplyToAddress = email.replyToAddress,
            segments = targetEmails,

            // Scheduling
            sendAfter = email.scheduledTime?.sendAfter,
            delayedOption = email.scheduledTime?.delayedOption,
            deliveryTimeOfDay = email.scheduledTime?.deliveryTimeOfDay
        )
        return postEmailNotification(payload)
    }




    private suspend fun postEmailNotification(payload: EmailPayload): Result<String> {
        return try {
            val response = httpClient.post(urlString = emailPushedEndPoint) {
                header(HttpHeaders.Authorization, "Basic ${config.apiKey}")
                contentType(ContentType.Application.Json)
                setBody(payload)
            }

            if (response.status.isSuccess()) {
                val result = response.body<OneSignalResponse>()
                if (!result.id.isNullOrBlank() && result.errors == null) {
                    println("Email Request Successful: Email MessageID ${result.id}")
                    Result.success(result.id)
                } else if (result.errors != null) {
                    Result.failure(OneSignalException.PayloadError(result.errors, result.id))
                } else {
                    Result.failure(OneSignalException.UnknownException("No Email message ID returned from OneSignal."))
                }
            } else {
                val errorText = response.bodyAsText()
                Result.failure(OneSignalException.ApiRequestError(response.status.value, errorText))
            }
        } catch (e: Exception) {
            Result.failure(OneSignalException.NetworkException(e))
        }



//            if (response.status.isSuccess()) {
//                println("Email Request Successful: ${response.status}")
//            } else {
//                println("Email Request Failed: ${response.status} - ${response.bodyAsText()}")
//            }
//
//        } catch (e: Exception) {
//            println("Error posting email notification: ${e.message}")
//            e.printStackTrace()
//        }
    }




}