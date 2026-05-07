package onesignal.feature_push

import com.onesignal.OneSignal
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener

actual suspend fun requestNotificationPermission() {
    OneSignal.Notifications.requestPermission(true)
}

actual fun listenForNotificationClicks(onClick: (title: String, body: String) -> Unit) {
    val listener = object : INotificationClickListener {
        override fun onClick(event: INotificationClickEvent) {
            val title = event.notification.title ?: "No Title"
            val body = event.notification.body ?: "No Body"

            // Send the data back to your KMP common code
            onClick(title, body)
        }
    }

    OneSignal.Notifications.addClickListener(listener)
}
