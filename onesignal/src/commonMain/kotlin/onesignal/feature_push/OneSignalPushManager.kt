package onesignal.feature_push

internal expect suspend fun requestNotificationPermission()

expect fun listenForNotificationClicks(onClick: (title: String, body: String) -> Unit)