package onesignal.feature_push

import kotlinx.cinterop.ExperimentalForeignApi
import onesignal.core.network.OneSignalClient
import platform.darwin.NSObject
import cocoapods.OneSignalXCFramework.OSNotificationClickListenerProtocol
import cocoapods.OneSignalXCFramework.OSNotificationClickEvent




internal actual suspend fun requestNotificationPermission() {

}



@OptIn(ExperimentalForeignApi::class)
actual fun listenForNotificationClicks(onClick: (title: String, body: String) -> Unit) {

    // Create a native iOS listener object
    val listener = object : NSObject(), OSNotificationClickListenerProtocol {
        override fun onClickNotification(event: OSNotificationClickEvent?) {
            val title = event?.notification?.title ?: "No Title"
            val body = event?.notification?.body ?: "No Body"

            onClick(title, body)
        }
    }

    // Attach it to the v5 iOS SDK
    OneSignalClient.Notifications.addClickListener(listener)
}




