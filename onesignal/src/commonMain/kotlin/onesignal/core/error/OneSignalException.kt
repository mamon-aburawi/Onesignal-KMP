package onesignal.core.error

import onesignal.feature_push.domain.model.PushError

sealed class OneSignalException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    /** Thrown when the HTTP request itself fails (e.g., 400 Bad Request, 401 Unauthorized) */
    class ApiRequestError(val statusCode: Int, val errorBody: String) :
        OneSignalException("OneSignal API rejected request ($statusCode): $errorBody")

    /** Thrown when HTTP is 200 OK, but OneSignal's JSON body reports errors (e.g., invalid aliases) */
    class PayloadError(val errors: PushError?, val partialId: String? = null) :
        OneSignalException("OneSignal reported payload errors: $errors")

    /** Thrown when Ktor fails to execute the network call (e.g., no internet connection, timeouts) */
    class NetworkException(cause: Throwable) :
        OneSignalException("Network/Ktor error: ${cause.message}", cause)

    /** Thrown for unexpected states, like a missing notification ID */
    class UnknownException(message: String) :
        OneSignalException(message)
}