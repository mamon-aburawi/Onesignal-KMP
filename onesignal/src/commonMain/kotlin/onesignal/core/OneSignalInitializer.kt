package onesignal.core


expect fun initOneSignal(appId: String)

internal expect suspend fun getSubscriptionId(): String?

internal expect suspend fun getOneSignalId(): String?