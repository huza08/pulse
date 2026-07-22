package app.pulse.desktop.ui.constants.sizes

// Card size config — change numbers here to resize all home cards
// Only constants actually referenced outside this file.
object CardSizes {
    // unified card sizes
    const val cardW = 180
    const val cardTextH = 90
    const val cardThumbRadius = 8
    const val cardEndPad = 24

    // text area gaps
    const val thumbTitleGap = 8
    const val titleArtistGap = 4

    // card inner padding (horizontal)
    const val cardInnerPad = 10

    // compact song card
    const val compactSongW = cardW
    const val compactSongEndPad = cardEndPad
    const val compactSongInnerPad = cardInnerPad
    const val compactSongTitle = 20
    const val compactSongArt = 18

    // album card
    const val albumW = cardW
    const val albumEndPad = cardEndPad
    const val albumInnerPad = cardInnerPad
    const val albumTitle = 20
    const val albumAuthor = 18

    // artist card
    const val artistName = 20
    const val artistVertPad = cardInnerPad

    // playlist card
    const val playlistInnerPad = cardInnerPad
    const val playlistName = 20
    const val playlistCount = 18

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
    const val trendingSub = 18
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
    const val gridGap = 24
    const val gridMinCardW = cardW
    const val gridThumbRadius = cardThumbRadius
    const val gridTitleFont = 20
    const val gridArtistFont = 18
    const val gridTextGapSm = titleArtistGap

    // skeleton — repeat counts
    const val skelMoodCount = 4
    const val skelAlbumCount = 4
    const val skelTrendingCount = 6
    const val skelQpSongCount = 5
    const val skelQpAlbumCount = 5

    // skeleton — shimmer sizes
    const val skelSectionH = 18
    const val skelMoodW = 180
    const val skelMoodH = 56
    const val skelMoodEndPad = 8
    const val skelAlbumW = 180
    const val skelAlbumEndPad = 24
    const val skelAlbumNameH = 18
    const val skelAlbumAuthorH = 12

    // skeleton — section title shimmer widths
    const val skelTitleWide = 180
    const val skelTitleMid = 180
    const val skelTitleNarrow = 180

    // skeleton — text placeholder widths
    const val skelTrendingTitleW = 180
    const val skelTrendingArtistW = 100
    const val skelTrendingDurW = 40
    const val skelAlbumNameW = 120
    const val skelAlbumAuthorW = 80

    // skeleton — gaps between text lines
    const val skelTextGapSm = 4

    // skeleton — thumbnail corner radiuses
    const val skelMoodRadius = 12
    const val skelAlbumRadius = 8

    // skeleton — shimmer rounded default radius
    const val skelShimmerRadius = 4
}
