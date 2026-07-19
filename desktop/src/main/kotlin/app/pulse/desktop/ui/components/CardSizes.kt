package app.pulse.desktop.ui.components

// card size config — change numbers here to resize all home cards
object CardSizes {
    // unified card sizes
    const val cardW = 160
    const val cardTextH = 90   // text area below thumbnail
    const val cardThumbRadius = 8
    const val cardEndPad = 10

    // unified text area gaps for all cards
    const val thumbTitleGap = 8   // thumbnail bottom to title text
    const val titleArtistGap = 4   // title text to artist/author

    // compact songcard now use unified sizes above
    const val compactSongW = cardW
    const val compactSongEndPad = cardEndPad
    const val compactSongInnerPad = 10   // horizontal padding only
    const val compactSongTitle = 20
    const val compactSongArt = 18

    // album card (new releases, related albums)
    const val albumW = cardW
    const val albumEndPad = cardEndPad
    const val albumInnerPad = 1
    const val albumTitle = 20
    const val albumAuthor = 18

    // artist card (similar artists)
    const val artistW = cardW
    const val artistEndPad = cardEndPad
    const val artistName = 20
    const val artistVertPad = 18

    // playlist card (recommended playlists)
    const val playlistW = cardW
    const val playlistEndPad = 14
    const val playlistInnerPad = 12
    const val playlistName = 18
    const val playlistCount = 15

    // mood/genre card
    const val moodW = 180
    const val moodH = 56
    const val moodFont = 16
    const val moodEndPad = 8
    const val moodInnerStart = 16

    // trending song card (SongCard component)
    const val trendingThumb = 96
    const val trendingRowPad = 10
    const val trendingBottomPad = 6
    const val trendingGap = 12
    const val trendingTitle = 20
    const val trendingSub = 20
    const val trendingDurStartPad = 8

    // section header
    const val headerTitle = 28
    const val headerMore = 18

    // section spacing
    const val gapSm = 8
    const val gapMd = 12
    const val gapLg = 16
    const val gapXl = 20

    // trending grids
    const val gridGap = 10
    const val gridMinCardW = cardW
    const val gridMaxCols = 6
    const val gridMinCols = 4
    const val gridThumbRadius = cardThumbRadius
    const val gridTitleFont = 15
    const val gridArtistFont = 13
    const val gridTextAreaH = cardTextH
    const val gridTextGapSm = titleArtistGap
    const val gridBottomPad = 2

    // skeleton — repeat counts
    const val skelMoodCount = 4
    const val skelAlbumCount = 4
    const val skelTrendingCount = 6
    const val skelQpSongCount = 5
    const val skelQpAlbumCount = 5

    // skeleton — shimmer sizes (derived from real card sizes)
    const val skelSectionH = 18

    const val skelMoodW = moodW
    const val skelMoodH = moodH
    const val skelMoodEndPad = moodEndPad

    const val skelAlbumW = albumW
    const val skelAlbumEndPad = albumEndPad
    const val skelAlbumNameH = 18
    const val skelAlbumAuthorH = 12

    const val skelTrendingThumb = trendingThumb
    const val skelTrendingBottomPad = trendingBottomPad
    const val skelTrendingGap = trendingGap
    const val skelTrendingTitleH = trendingTitle
    const val skelTrendingSubH = trendingSub
    const val skelTrendingDurStartPad = trendingDurStartPad

    // skeleton — section title shimmer widths (derived from card sizes)
    const val skelTitleWide = compactSongW
    const val skelTitleMid = moodW
    const val skelTitleNarrow = compactSongW

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
