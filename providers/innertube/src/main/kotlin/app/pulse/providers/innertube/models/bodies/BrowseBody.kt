package app.pulse.providers.innertube.models.bodies

import app.pulse.providers.innertube.models.Context
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BrowseBody(
    val context: Context = Context.DefaultWeb,
    val browseId: String,
    val params: String? = null,
    @SerialName("formData")
    val formData: FormData? = null
) {
    @Serializable
    data class FormData(
        val selectedValues: List<String>
    )
}
