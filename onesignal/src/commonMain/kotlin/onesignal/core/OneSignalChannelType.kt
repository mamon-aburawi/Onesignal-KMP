package onesignal.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class OneSignalChannelType {
    @SerialName("email")
    EMAIL,

    @SerialName("push")
    PUSH
}