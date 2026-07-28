package app.pulse.providers.innertube.requests

import app.pulse.providers.innertube.Innertube
import app.pulse.providers.innertube.models.BrowseResponse
import app.pulse.providers.innertube.models.Context
import app.pulse.providers.innertube.models.MusicCarouselShelfRenderer
import app.pulse.providers.innertube.models.NextResponse
import app.pulse.providers.innertube.models.bodies.BrowseBody
import app.pulse.providers.innertube.models.bodies.NextBody
import app.pulse.providers.innertube.utils.findSectionByStrapline
import app.pulse.providers.innertube.utils.findSectionByTitle
import app.pulse.providers.innertube.utils.from
import app.pulse.providers.utils.runCatchingCancellable
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
suspend fun Innertube.relatedPage(body: NextBody) = runCatchingCancellable {
    val nextResponse = client.post(NEXT) {
        setBody(body.copy(context = Context.DefaultWebNoLang))
        @Suppress("all")
        mask(
            "contents.singleColumnMusicWatchNextResultsRenderer.tabbedRenderer.watchNextTabbedResultsRenderer.tabs.tabRenderer(endpoint,title)"
        )
    }.body<NextResponse>()

    // capture tab info before extracting browseId (used in both paths)
    val tabs = nextResponse
        .contents?.singleColumnMusicWatchNextResultsRenderer
        ?.tabbedRenderer?.watchNextTabbedResultsRenderer
        ?.tabs
    val browseId = tabs
        ?.firstOrNull { tab ->
            tab.tabRenderer?.endpoint?.browseEndpoint?.browseId?.startsWith("MPTR") == true
        }
        ?.tabRenderer
        ?.endpoint
        ?.browseEndpoint
        ?.browseId

    if (browseId == null) {
        return@runCatchingCancellable null
    }

    val response = client.post(BROWSE) {
        setBody(
            BrowseBody(
                browseId = browseId,
                context = Context.DefaultWebNoLang
            )
        )
        @Suppress("all")
        mask(
            "contents.sectionListRenderer.contents.musicCarouselShelfRenderer(header.musicCarouselShelfBasicHeaderRenderer(title,strapline),contents($MUSIC_RESPONSIVE_LIST_ITEM_RENDERER_MASK,$MUSIC_TWO_ROW_ITEM_RENDERER_MASK))"
        )
    }.body<BrowseResponse>()

    val sectionListRenderer = response
        .contents
        ?.sectionListRenderer

    val songs = sectionListRenderer
        ?.findSectionByTitle("You might also like")
        ?.musicCarouselShelfRenderer
        ?.contents
        ?.mapNotNull(MusicCarouselShelfRenderer.Content::musicResponsiveListItemRenderer)
        ?.mapNotNull(Innertube.SongItem::from)

    val playlists = sectionListRenderer
        ?.findSectionByTitle("Recommended playlists")
        ?.musicCarouselShelfRenderer
        ?.contents
        ?.mapNotNull(MusicCarouselShelfRenderer.Content::musicTwoRowItemRenderer)
        ?.mapNotNull(Innertube.PlaylistItem::from)
        ?.sortedByDescending { it.channel?.name == "YouTube Music" }

    val albums = sectionListRenderer
        ?.findSectionByStrapline("MORE FROM")
        ?.musicCarouselShelfRenderer
        ?.contents
        ?.mapNotNull(MusicCarouselShelfRenderer.Content::musicTwoRowItemRenderer)
        ?.mapNotNull(Innertube.AlbumItem::from)

    val artists = sectionListRenderer
        ?.findSectionByTitle("Similar artists")
        ?.musicCarouselShelfRenderer
        ?.contents
        ?.mapNotNull(MusicCarouselShelfRenderer.Content::musicTwoRowItemRenderer)
        ?.mapNotNull(Innertube.ArtistItem::from)

    Innertube.RelatedPage(
        songs = songs,
        playlists = playlists,
        albums = albums,
        artists = artists
    )
}
