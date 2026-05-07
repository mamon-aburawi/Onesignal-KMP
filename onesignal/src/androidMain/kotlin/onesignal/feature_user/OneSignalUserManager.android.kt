package onesignal.feature_user

import com.onesignal.OneSignal



actual fun loginOneSignalUser(userId: String) {
    OneSignal.login(externalId = userId)
}

actual suspend fun logoutOneSignalUser() {
    OneSignal.logout()
}