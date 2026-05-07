package onesignal.feature_push.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject


@Serializable
data class PushError(
    @SerialName("invalid_player_ids")
    val invalidPlayerIds: List<String>? = null,

    @SerialName("invalid_aliases")
    val invalidAliases: JsonObject? = null
)

