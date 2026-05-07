package onesignal.feature_email

import kotlinx.cinterop.ExperimentalForeignApi
import onesignal.core.network.OneSignalClient


@OptIn(ExperimentalForeignApi::class)
internal actual fun addEmailToOneSignal(email: String) {
    OneSignalClient.User.addEmail(email)
}


@OptIn(ExperimentalForeignApi::class)
internal actual fun removeEmailFromOneSignal(email: String) {
    OneSignalClient.User.removeEmail(email)
}

