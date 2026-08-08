package app.pulse.desktop.ui.constants.sizes

// sizes for the right sidebar — own constants object (like LeftSidebar),
// so resizing the right panel doesn't touch the shared Sizes object.
object RightSidebar {

    // panel widths
    const val targetWidth = 380
    const val minWidth = 360
    const val maxWidth = 400
    const val collapsedWidth = 48
    const val intermediateWidth = 140

    // outer/section padding — also aligns TopNavBar + queue panel
    const val padding = 24
    // vertical section padding (collapsed icon strip)
    const val sectionPad = 24

    // card
    const val cardRadius = 12
    const val cardInnerPad = 16
    const val thumbRadius = 6
    const val cardContentGap = 8
    const val songArtistGap = 4
    const val itemGap = 8
    const val chevronSpacer = 8
    const val queueThumb = 64

    // icons
    const val iconSm = 28
    const val creditIcon = 24
    const val ellipsisIcon = 24
    const val shareIcon = 24
    const val chevronIcon = 28
}
