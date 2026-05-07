package onesignal.core

import com.onesignal.OneSignal
import kotlinx.coroutines.DelicateCoroutinesApi
import onesignal.provider.OneSignalContextProvider



@OptIn(DelicateCoroutinesApi::class)
actual fun initOneSignal(appId: String) {
    val context = OneSignalContextProvider.applicationContext
        ?: throw IllegalStateException("OneSignalInitProvider failed to grab Context! Check AndroidManifest.")

    OneSignal.initWithContext(context, appId)
}


actual suspend fun getSubscriptionId(): String?{
    return OneSignal.User.pushSubscription.id
}

internal actual suspend fun getOneSignalId(): String? {
    return OneSignal.User.onesignalId
}