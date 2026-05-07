package onesignal.core.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import onesignal.feature_push.domain.model.PushError


@Serializable
data class OneSignalResponse(
    val id: String? = null,

    @SerialName("external_id")
    val externalId: String? = null,

    val errors: PushError? = null,
    val recipients: Int? = null
)



