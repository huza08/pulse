package app.pulse.providers.innertube.requests

import app.pulse.providers.innertube.Innertube
import app.pulse.providers.innertube.models.BrowseResponse
import app.pulse.providers.innertube.models.bodies.BrowseBody
import app.pulse.providers.innertube.utils.from
import app.pulse.providers.utils.runCatchingCancellable
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import java.util.Locale

suspend fun Innertube.trendingCharts(
    country: String = Locale.getDefault().country.takeIf { it.length == 2 } ?: "US"
) = runCatchingCancellable {
    val response = client.post(BROWSE) {
        setBody(
            BrowseBody(
                browseId = "FEmusic_charts",
                formData = BrowseBody.FormData(selectedValues = listOf(country))
            )
        )
        mask("contents")
    }.body<BrowseResponse>()

    val sections = response
        .contents
        ?.singleColumnBrowseResultsRenderer
        ?.tabs
        ?.firstOrNull()
        ?.tabRenderer
        ?.content
        ?.sectionListRenderer
        ?.contents

    val trending = sections?.find {
        it.musicCarouselShelfRenderer
            ?.header
            ?.musicCarouselShelfBasicHeaderRenderer
            ?.moreContentButton
            ?.buttonRenderer
            ?.navigationEndpoint
            ?.browseEndpoint
            ?.browseEndpointContextSupportedConfigs
            ?.browseEndpointContextMusicConfig
            ?.pageType == "MUSIC_PAGE_TYPE_PLAYLIST"
    }?.musicCarouselShelfRenderer

    trending?.toBrowseItem(Innertube.SongItem::from)
        ?.items
        ?.filterIsInstance<Innertube.SongItem>()
        ?.map { song ->
            song.copy(
                authors = song.authors?.firstOrNull()?.let { listOf(it) } ?: emptyList()
            )
        }
        .orEmpty()
}
