package onesignal.feature_push



actual suspend fun requestNotificationPermission() {
    println("OneSignal Warning: Notification permissions are not supported on JVM Desktop.")
}


actual fun listenForNotificationClicks(onClick: (title: String, body: String) -> Unit) {
    println("OneSignal Warning: Notification click listeners are not supported on JVM Desktop.")
}
