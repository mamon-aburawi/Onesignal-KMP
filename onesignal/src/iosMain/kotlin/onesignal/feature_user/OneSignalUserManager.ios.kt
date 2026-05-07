package onesignal.feature_user

import kotlinx.cinterop.ExperimentalForeignApi
import onesignal.core.network.OneSignalClient


@OptIn(ExperimentalForeignApi::class)
actual fun loginOneSignalUser(userId: String) {
    OneSignalClient.login(userId)
}

actual suspend fun logoutOneSignalUser() {
    OneSignalClient.logout()
}
