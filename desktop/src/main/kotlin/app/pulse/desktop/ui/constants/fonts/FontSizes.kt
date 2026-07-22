package app.pulse.desktop.ui.constants.fonts

// Unified font sizes for the desktop module
// Change numbers here to resize text app-wide
object FontSizes {
    // searchbar
    const val searchbar = 18

    // queue panel (matched to right sidebar sizes)
    const val queueTitle = 14
    const val queueSub = 16      // matches rightLabel
    const val queueMeta = 14     // matches rightNextSub, rightArtist
    const val queueSmall = 10

    // left sidebar
    const val sidebarSection = 20
    const val sidebarItem = 16
    const val sidebarChip = 15
    const val sidebarSmall = 16
    const val sidebarSub = 14

    // right sidebar (matched to left sidebar sizes)
    const val rightSection = 20    // matches sidebarSection
    const val rightSongTitle = 24  // matches sidebarItem
    const val rightArtist = 18     // matches sidebarSub
    const val rightCredit = 16     // matches sidebarSmall
    const val rightLabel = 16      // matches sidebarItem
    const val rightCreditsArtist = 16  // matches sidebarSub
    const val rightNextSub = 16    // matches sidebarSub
}
