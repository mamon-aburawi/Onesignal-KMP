@file:OptIn(ExperimentalWasmJsInterop::class)

package onesignal.feature_push

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

actual suspend fun requestNotificationPermission() {
    requestPermissionJS()
}

actual fun listenForNotificationClicks(onClick: (title: String, body: String) -> Unit) {
    val callback: (String, String) -> Unit = { title, body -> onClick(title, body) }
    setupWebListenersJS(callback)
}


private fun requestPermissionJS(): Unit = js("""{
    window.OneSignalDeferred = window.OneSignalDeferred || [];
    window.OneSignalDeferred.push(function(OneSignal) {
        // Triggers the native browser prompt (Allow/Block)
        OneSignal.Notifications.requestPermission();
    });
}""")


private fun setupWebListenersJS(onEvent: (String, String) -> Unit): Unit = js("""{
    window.OneSignalDeferred = window.OneSignalDeferred || [];

    // Register the Kotlin function globally so JS can trigger it
    window._osKotlinListener = onEvent;

    window.OneSignalDeferred.push(function(OneSignal) {

        // Listen for the click event (OneSignal v16 Web API)
        OneSignal.Notifications.addEventListener('click', function(event) {
            var title = "No Title";
            var body = "No Body";

            try {
                // Safely extract the title and body
                if (event && event.notification) {
                    title = event.notification.title || "No Title";
                    body = event.notification.body || "No Body";
                }

                // Check if the user clicked a specific Action Button!
                if (event.result && event.result.actionId) {
                    body = body + " (Action Clicked: " + event.result.actionId + ")";
                }
            } catch (e) {
                console.error("OneSignal Click Parse Error: ", e);
            }

            // Trigger the Kotlin Compose UI!
            if (window._osKotlinListener) {
                window._osKotlinListener(title, body);
            }
        });
    });
}""")

