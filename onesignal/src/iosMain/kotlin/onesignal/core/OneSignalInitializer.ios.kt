package onesignal.core

import kotlinx.cinterop.ExperimentalForeignApi
import onesignal.core.network.OneSignalClient

@OptIn(ExperimentalForeignApi::class)
actual fun initOneSignal(appId: String) {
    OneSignalClient.initialize(appId, withLaunchOptions = null)
}

@OptIn(ExperimentalForeignApi::class)
internal actual suspend fun getSubscriptionId(): String? {
    return OneSignalClient.User.pushSubscription.id
}

internal actual suspend fun getOneSignalId(): String? {
    OneSignal.User.onesignalId
}