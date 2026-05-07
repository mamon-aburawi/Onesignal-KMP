package onesignal.core

actual fun initOneSignal(appId: String) {
    println("OneSignal Warning: Init Onesignal is not supported on JVM Desktop.")
}

internal actual suspend fun getSubscriptionId(): String? {
    println("OneSignal Warning: Getting Subscription ID is not supported on JVM Desktop.")
    return null
}


internal actual suspend fun getOneSignalId(): String? {
    println("OneSignal Warning: Getting OneSignal ID is not supported on JVM Desktop.")
    return null
}