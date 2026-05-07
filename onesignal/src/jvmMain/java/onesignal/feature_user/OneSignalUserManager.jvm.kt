package onesignal.feature_user


internal actual fun loginOneSignalUser(userId: String) {
    println("OneSignal Warning: Login is not supported on JVM Desktop.")
}


actual suspend fun logoutOneSignalUser() {
    println("OneSignal Warning: Logout is not supported on JVM Desktop.")
}
