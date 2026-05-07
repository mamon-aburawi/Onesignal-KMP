@file:OptIn(ExperimentalWasmJsInterop::class)

package onesignal.feature_user

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js


actual fun loginOneSignalUser(userId: String) {
    loginOneSignalWebJS(userId)
}

actual suspend fun logoutOneSignalUser() {
    logoutOneSignalUserJS()
}


private fun loginOneSignalWebJS(userId: String): Unit = js("""{
    window.OneSignalDeferred = window.OneSignalDeferred || [];
    window.OneSignalDeferred.push(function(OneSignal) {
     
        OneSignal.login(userId);
        
        if (OneSignal.User && OneSignal.User.PushSubscription) {
            OneSignal.User.PushSubscription.optIn();
            console.log("OneSignal: Logged in and forced Push Subscription opt-in for " + userId);
        }
        
    });
}""")


private fun logoutOneSignalUserJS(): Unit = js("""{
    window.OneSignalDeferred = window.OneSignalDeferred || [];
    window.OneSignalDeferred.push(function(OneSignal) {
        
        // Tells OneSignal v16 to remove the External ID from this browser
        OneSignal.logout();
        
        console.log("OneSignal: User successfully logged out!");
    });
}""")
