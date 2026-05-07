package onesignal.feature_push.domain

import onesignal.feature_push.domain.model.OneSignalNotification

interface PushNotificationRepository {


    suspend fun sendNotification(params: OneSignalNotification, externalIds: List<String>): Result<String>

    suspend fun sendNotification(params: OneSignalNotification, externalId: String): Result<String>


    suspend fun sendNotification(subscriptionIds: List<String>, params: OneSignalNotification): Result<String>

    suspend fun sendNotification(subscriptionId: String, params: OneSignalNotification): Result<String>

    suspend fun sendNotificationToAll(params: OneSignalNotification): Result<String>



}