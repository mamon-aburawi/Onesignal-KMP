package onesignal.feature_email

import com.onesignal.OneSignal



internal actual fun addEmailToOneSignal(email: String) {
    OneSignal.User.addEmail(email)
}

internal actual fun removeEmailFromOneSignal(email: String) {
    OneSignal.User.removeEmail(email)
}

