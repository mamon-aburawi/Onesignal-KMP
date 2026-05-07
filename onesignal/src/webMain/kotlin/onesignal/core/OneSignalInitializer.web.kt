@file:OptIn(ExperimentalWasmJsInterop::class)

package onesignal.core

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js


actual fun initOneSignal(appId: String) {
    injectOneSignalJS(appId)
}


internal actual suspend fun getOneSignalId(): String? {
    return suspendCoroutine { continuation ->
        fetchWebOneSignalIdAsync { id ->
            continuation.resume(id)
        }
    }
}


actual suspend fun getSubscriptionId(): String? {
    return suspendCoroutine { continuation ->
        val jsCallback: (String?) -> Unit = { id ->
            continuation.resume(id)
        }
        fetchWebSubscriptionIdAsync(jsCallback)
    }
}



private fun injectOneSignalJS(appId: String): Unit = js("""{
    window.OneSignalDeferred = window.OneSignalDeferred || [];
    window.OneSignalDeferred.push(async function(OneSignal) {
        await OneSignal.init({
            appId: appId,
            allowLocalhostAsSecureOrigin: true
        });


        console.log("My Web Subscription ID: ", OneSignal.User.PushSubscription.id);
    });
}""")


private fun fetchWebOneSignalIdAsync(callback: (String?) -> Unit) {
    js("""
        window.OneSignalDeferred = window.OneSignalDeferred || [];
        window.OneSignalDeferred.push(function(OneSignal) {
            var userId = OneSignal.User.onesignalId;
            callback(userId ? userId : null);
        });
    """)
}


private fun fetchWebSubscriptionIdAsync(callback: (String?) -> Unit): Unit = js("""
    {
        window.OneSignalDeferred = window.OneSignalDeferred || [];
        window.OneSignalDeferred.push(function(OneSignal) {
            var subId = OneSignal.User.PushSubscription.id;
            callback(subId ? subId : null);
        });
    }
""")
