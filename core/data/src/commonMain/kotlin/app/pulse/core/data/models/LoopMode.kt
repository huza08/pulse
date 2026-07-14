package app.pulse.core.data.models

import kotlinx.serialization.Serializable

@Serializable
enum class LoopMode {
    NONE, ONE, ALL
}
