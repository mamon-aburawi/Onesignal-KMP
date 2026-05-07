package onesignal.feature_email.domain

import onesignal.feature_email.domain.model.OneSignalEmail

interface PushEmailRepository {

    suspend fun sendEmailToAll(email: OneSignalEmail): Result<String>

    suspend fun sendEmail(email: OneSignalEmail, targetEmail: String): Result<String>

    suspend fun sendEmail(email: OneSignalEmail, targetEmails: List<String>): Result<String>

}