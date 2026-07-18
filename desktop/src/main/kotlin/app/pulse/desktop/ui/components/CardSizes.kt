package app.pulse.desktop.ui.components

// card size config — change numbers here to resize all home cards
object CardSizes {
    // compact song card (quick picks)
    const val compactSongW = 160
    const val compactSongEndPad = 10
    const val compactSongInnerPad = 10
    const val compactSongTitle = 18
    const val compactSongArt = 15

    // album card (new releases, related albums)
    const val albumW = 180
    const val albumEndPad = 14
    const val albumInnerPad = 12
    const val albumTitle = 18
    const val albumAuthor = 15

    // artist card (similar artists)
    const val artistW = 160
    const val artistEndPad = 14
    const val artistThumb = 96
    const val artistName = 18
    const val artistVertPad = 16

    // playlist card (recommended playlists)
    const val playlistW = 180
    const val playlistEndPad = 14
    const val playlistInnerPad = 12
    const val playlistName = 18
    const val playlistCount = 15

    // mood/genre card
    const val moodW = 140
    const val moodH = 56
    const val moodFont = 13
    const val moodEndPad = 8
    const val moodInnerStart = 16

    // trending song card (SongCard component)
    const val trendingThumb = 72
    const val trendingRowPad = 10
    const val trendingBottomPad = 6
    const val trendingGap = 12
    const val trendingTitle = 16
    const val trendingSub = 14
    const val trendingDurStartPad = 8

    // section header
    const val headerTitle = 18
    const val headerMore = 13

    // section spacing
    const val gapSm = 8
    const val gapMd = 12
    const val gapLg = 16
    const val gapXl = 28

    // skeleton — repeat counts
    const val skelMoodCount = 4
    const val skelAlbumCount = 4
    const val skelTrendingCount = 6

    // skeleton — shimmer sizes (derived from real card sizes)
    const val skelSectionH = 18

    const val skelMoodW = moodW
    const val skelMoodH = moodH
    const val skelMoodEndPad = moodEndPad

    const val skelAlbumW = albumW
    const val skelAlbumEndPad = albumEndPad
    const val skelAlbumNameH = 14
    const val skelAlbumAuthorH = 11

    const val skelTrendingThumb = trendingThumb
    const val skelTrendingBottomPad = trendingBottomPad
    const val skelTrendingGap = trendingGap
    const val skelTrendingTitleH = trendingTitle
    const val skelTrendingSubH = trendingSub
    const val skelTrendingDurStartPad = trendingDurStartPad

    // skeleton — section title shimmer widths (derived from card sizes)
    const val skelTitleWide = compactSongW
    const val skelTitleMid = moodW
    const val skelTitleNarrow = compactSongW - 40

    // skeleton — text placeholder widths
    const val skelTrendingTitleW = 180
    const val skelTrendingArtistW = 100
    const val skelTrendingDurW = 40
    const val skelAlbumNameW = 120
    const val skelAlbumAuthorW = 80

    // skeleton — gaps between text lines
    const val skelTextGap = 6
    const val skelTextGapSm = 4

    // skeleton — thumbnail corner radiuses
    const val skelMoodRadius = 12
    const val skelAlbumRadius = 8
    const val skelTrendingRadius = 6

    // skeleton — shimmer rounded default radius
    const val skelShimmerRadius = 4
}
