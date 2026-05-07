@file:OptIn(ExperimentalWasmJsInterop::class)

package onesignal.feature_email

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js


internal actual fun addEmailToOneSignal(email: String) {
    addEmailOneSignalWebJS(email)
}


actual fun removeEmailFromOneSignal(email: String) {
    removeEmailOneSignalWebJS(email)
}



private fun addEmailOneSignalWebJS(email: String): Unit = js("""{
    window.OneSignalDeferred = window.OneSignalDeferred || [];
    window.OneSignalDeferred.push(function(OneSignal) {
     
        if (OneSignal.User) {
            OneSignal.User.addEmail(email);
            console.log("OneSignal: Successfully added email " + email);
        } else {
            console.error("OneSignal: User object is not available yet.");
        }
        
    });
}""")


private fun removeEmailOneSignalWebJS(email: String): Unit = js("""{
    window.OneSignalDeferred = window.OneSignalDeferred || [];
    window.OneSignalDeferred.push(function(OneSignal) {
     
        if (OneSignal.User) {
            OneSignal.User.removeEmail(email);
            console.log("OneSignal: Successfully removed email " + email);
        }
        
    });
}""")

