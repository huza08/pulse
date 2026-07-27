package app.pulse.desktop.ui.constants.sizes

// unified layout sizes for the desktop module.
// change related constants together when resizing.
object Sizes {

    // layout shell - sidebar / panel containers

    // left sidebar expanded width
    const val sidebarTargetWidth = 380
    // left sidebar minimum resize width
    const val sidebarMinWidth = 360
    // left sidebar maximum resize width
    const val sidebarMaxWidth = 400
    // collapsed sidebar width, also drag threshold
    const val sidebarCollapsedDrag = 96
    // width when expanding from collapsed
    const val sidebarRestoreWidth = 340
    // right panel default width
    const val panelTargetWidth = sidebarTargetWidth
    const val panelMinWidth = sidebarMinWidth
    const val panelMaxWidth = sidebarMaxWidth

    // responsive thresholds

    // drag distance before uncollapsing
    const val sidebarUncollapseThreshold = 120
    // min width to show your library text + filter pills
    const val sidebarWideThreshold = 250
    // min center content width
    const val centerMinWidth = 128

    // left sidebar - icons, thumbnails, spacing

    // playlist/artist thumbnail size
    const val sidebarThumbSize = 64
    // large header icons
    const val sidebarIconLg = 36
    // search + list icons
    const val sidebarIconMd = 38
    // toggle, +, expand, and collapsed overlay icons
    // update sidebarOverlayH + sidebarFixedOffset when this changes
    const val sidebarIconSm = 24
    // liked songs heart inside thumbnail
    const val sidebarHeartIcon = 22
    // pinned heart next to playlist name
    const val sidebarPinnedIcon = 14

    // row heights

    // search + recents + sort row height
    const val sidebarSearchRowH = 40

    // spacing & padding

    // horizontal gap between items
    const val sidebarItemGap = 8
    // vertical padding inside clickable items
    const val sidebarItemPadV = 4
    // horizontal padding between icon and label
    const val sidebarItemPadH = 10
    // outer horizontal padding of sidebar content
    const val sidebarOuterPadH = 8
    // filter-chip horizontal padding
    const val sidebarFilterPadH = 16
    // create (+) icon padding
    const val sidebarIconPad = 6

    // section spacing

    // top padding of your library header
    const val sidebarHeaderTop = 28
    // space below your library header
    const val sidebarHeaderBottom = 16
    // gap between sections
    const val sidebarSectionGap = 16
    // outer sidebar padding
    const val sidebarPad = 24

    // derived layout offsets (update when icon sizes change)

    // collapsed spacer height = itemPadV + iconSm + sectionGap + iconSm + sectionGap = 84dp
    const val sidebarOverlayH = 84
    // horizontal offset for overlay alignment = (collapsedDrag - 2*outerPadH - iconSm) / 2 = 28dp
    const val sidebarFixedOffset = 28
    // end padding of section content column
    const val sidebarSectionEndPad = 16

    // right sidebar

    // collapsed width (thin strip)
    const val rightCollapsedWidth = 48
    // peeked width
    const val rightIntermediateWidth = 140
    // outer/section padding
    const val rightPanelPadding = 24
    // inner card padding
    const val rightCardInnerPad = 16
    // card corner radius
    const val rightCardRadius = 12
    // queue thumbnail size
    const val rightQueueThumb = 64
    // icon sizes
    const val rightCreditIcon = 24
    const val rightEllipsisIcon = 24
    const val rightShareIcon = 24
    const val rightChevronIcon = 24
    // thumbnail radius
    const val rightThumbRadius = 6
    // reuses sidebar item gap
    const val rightCardContentGap = sidebarItemGap
    // gap between song title and artist
    const val rightSongArtistGap = sidebarItemPadV
    // item-to-item gap
    const val rightItemGap = sidebarItemGap
    // chevron spacer
    const val rightChevronSpacer = sidebarItemGap

    // queue panel

    const val queueThumbSize = 64
    const val queueCloseIcon = 24
    const val queueRemoveIcon = 18
    const val queueReorderW = 20
    const val queueMinWidth = 300
    const val queueMaxWidth = 400
    const val queueThumbRadius = 6
    const val queueItemPadV = 8
    // header to close, header to divider, etc
    const val queueSpacerMd = 12
    // info to duration, duration to remove
    const val queueItemSpacerSm = 6

    // top navigation bar

    const val topBarHeight = 72
    const val searchMaxWidth = 768
    const val profileIconSize = 32
    const val searchIconSize = 20
    const val searchCornerRadius = 20
    const val searchTopPad = 16
    // profile avatar background inset
    const val topBarProfileBg = 5

    // common corners & resize handle

    const val radiusSm = 6     // list rows, thumbnails
    const val radiusMd = 8     // sidebar cards, center content
    const val radiusLg = 12    // reserved
    const val radiusPill = 16  // filter chips, search bar
    const val radiusXL = 24    // reserved
    const val resizerW = 16    // resize handle hit area
    const val resizerHintH = 0.4f  // handle hint line height fraction

    // re-show button (appears when collapsed)

    const val reShowBtnSize = 40
    const val reShowIconSize = 18

    // window constraints

    const val windowDefaultW = 1280
    const val windowDefaultH = 720
    const val windowMinW = 1280
    const val windowMinH = 720

    // shimmer (loading placeholders)

    // text placeholder line radius
    const val shimmerRadiusXs = 6

    // home screen

    // main column padding
    const val homeColumnPad = 24
    // bottom padding to clear miniplayer
    const val homeColumnBottomPad = 100
    // carousel edge fade width
    const val homeFadeWidth = 80
    // scrollbar
    const val scrollMinH = 16
    const val scrollThickness = 8
    const val scrollRadius = 4
    const val scrollPad = 8
    const val scrollEndPad = 2

    // player screen

    const val playerTopBarH = 80
    const val playerTopIconSize = 36
    const val playerTopTitleFont = 24
    const val playerTopIconGap = 14
    const val playerCardRadius = 16
    const val playerCardSize = 400
    const val playerPad = 32
    const val playerInfoFont = 20
    const val playerTitleFont = 26
    const val playerTitleGap = 16
    const val playerArtistGap = 4

    // miniplayer

    const val miniPlayerRadius = 12
    const val miniPlayerCardRadius = 64
    const val miniPlayerShadow = 12
    const val miniPlayerEndPad = 12
    const val miniPlayerBottomPad = 14
    const val miniPlayerH = 90
    const val miniPlayerThumb = 64
    const val miniPlayerThumbPad = 14
    const val miniPlayerTextStart = 20
    // between icon groups
    const val miniPlayerSpacerLg = 16
    // between adjacent icons
    const val miniPlayerSpacerSm = 8
    const val miniPlayerIconSm = 22      // shuffle, repeat
    const val miniPlayerIconMd = 32      // skip, play/pause
    const val miniPlayerIconLg = 60      // play button
    const val miniPlayerIconSide = 26    // lyrics, queue, volume, expand
    const val miniPlayerIconTitle = 18
    const val miniPlayerIconSub = 14
    const val miniPlayerSeekW = 560
    const val miniPlayerSeekTimeW = 34
    const val miniPlayerSeekTimeFont = 11
    const val miniPlayerVolW = 96
    const val miniPlayerSpacerSide = 14
    const val miniPlayerSpacerVol = 12
}
