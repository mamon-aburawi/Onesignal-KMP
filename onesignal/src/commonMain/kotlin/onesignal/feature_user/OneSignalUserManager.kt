package onesignal.feature_user

internal expect fun loginOneSignalUser(userId: String)

internal expect suspend fun logoutOneSignalUser()