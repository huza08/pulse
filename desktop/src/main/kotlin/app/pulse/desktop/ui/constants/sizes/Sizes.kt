package app.pulse.desktop.ui.constants.sizes

// unified spacing, thumb, and icon sizes for the desktop module
// change numbers here to resize layout elements app-wide
object Sizes {
    // layout shell
    const val sidebarTargetWidth = 380
    const val sidebarMinWidth = 360
    const val sidebarMaxWidth = 400
    const val sidebarCollapsedDrag = 96  // drag threshold for collapse / collapsed width
    const val sidebarRestoreWidth = 340 // width when uncollapsing, same looks
    const val panelTargetWidth = sidebarTargetWidth
    const val panelMinWidth = sidebarMinWidth
    const val panelMaxWidth = sidebarMaxWidth

    // left sidebar
    const val sidebarThumbSize = 64
    const val sidebarIconLg = 36      // header icons, collapsed icons
    const val sidebarIconMd = 38     // search/list icons
    const val sidebarIconSm = 24      // expand/collapse toggle
    const val sidebarHeartIcon = 22   // liked songs in thumb
    const val sidebarPinnedIcon = 14  // pinned heart in list
    const val sidebarSearchRowH = 40
    const val sidebarItemGap = 8      // Arrangement.spacedBy
    const val sidebarItemPadV = 4     // inner vertical padding
    const val sidebarItemPadH = 10    // inner horizontal padding
    const val sidebarOuterPadH = 8    // outer horizontal padding
    const val sidebarPad = 24          // dedicated sidebar panel padding (outer, gaps)
    const val sidebarIconPad = 6      // create icon padding
    const val sidebarHeaderTop = 20   // header title top padding
    const val sidebarHeaderBottom = 16 // top section bottom padding
    const val sidebarSectionGap = 16  // vertical gap between sidebar sections
    const val sidebarFilterPadH = 16  // filter row horizontal padding

    // right sidebar (matched to left sidebar sizes)
    const val rightCollapsedWidth = 48
    const val rightIntermediateWidth = 140
    const val rightPanelPadding = 24    // matches sidebarItemPadH (outer + section spacing)
    const val rightCardInnerPad = 16    // inner padding for credit/queue cards
    const val rightCardRadius = 12
    const val rightQueueThumb = 64
    const val rightCreditIcon = 24      // matches sidebarIconSm
    const val rightEllipsisIcon = 24    // matches sidebarIconSm
    const val rightShareIcon = 24       // matches sidebarIconSm
    const val rightChevronIcon = 24     // matches sidebarIconSm
    const val rightThumbRadius = 6
    const val rightCardContentGap = sidebarItemGap
    const val rightSongArtistGap = sidebarItemPadV
    const val rightItemGap = sidebarItemGap
    const val rightChevronSpacer = sidebarItemGap

    // queue
    const val queueThumbSize = 64
    const val queueCloseIcon = 24
    const val queueRemoveIcon = 18
    const val queueReorderW = 20
    const val queueMinWidth = 300
    const val queueMaxWidth = 400
    const val queueThumbRadius = 6  // matches rightThumbRadius
    const val queueItemPadV = 8     // matches rightItemGap
    const val queueSpacerMd = 12          // header→close, header→divider etc.
    const val queueItemSpacerSm = 6       // info→duration, duration→remove

    // topbar
    const val topBarHeight = 72
    const val searchMaxWidth = 768
    const val profileIconSize = 32
    const val searchIconSize = 20
    const val searchCornerRadius = 20
    const val searchTopPad = 16
    const val topBarProfileBg = 5

    // common corners
    const val radiusSm = 6
    const val radiusMd = 8
    const val radiusLg = 12
    const val radiusPill = 16
    const val radiusXL = 24
    const val resizerW = 16
    const val resizerHintH = 0.4f     // hint line height fraction

    // reshow
    const val reShowBtnSize = 40
    const val reShowIconSize = 18

    // layout shell thresholds
    const val sidebarUncollapseThreshold = 120
    const val sidebarWideThreshold = 250
    const val centerMinWidth = 128
    const val windowDefaultW = 1280
    const val windowDefaultH = 720
    const val windowMinW = 1280
    const val windowMinH = 720

    // shimmer
    const val shimmerRadiusXs = 6         // text placeholder lines

    // homescreen
    const val homeColumnPad = 24           // main column padding start/end/top
    const val homeColumnBottomPad = 100    // bottom padding for miniplayer clearance
    const val homeFadeWidth = 80           // carousel edge fade width
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
    const val playerTitleGap = 16           // between content and title
    const val playerArtistGap = 4          // between title and artist

    // miniplayer
    const val miniPlayerRadius = 12
    const val miniPlayerCardRadius = 64    // outer card shape
    const val miniPlayerShadow = 12
    const val miniPlayerEndPad = 12
    const val miniPlayerBottomPad = 14
    const val miniPlayerH = 90             // row height
    const val miniPlayerThumb = 64         // thumbnail
    const val miniPlayerThumbPad = 14      // start padding
    const val miniPlayerTextStart = 20     // spacer after thumb
    const val miniPlayerSpacerLg = 16      // between icon groups
    const val miniPlayerSpacerSm = 8       // between adjacent icons
    const val miniPlayerIconSm = 22        // shuffle, repeat
    const val miniPlayerIconMd = 32        // skip, play/pause
    const val miniPlayerIconLg = 60        // play button box
    const val miniPlayerIconSide = 26      // lyrics, queue, volume, expand
    const val miniPlayerIconTitle = 18     // title font
    const val miniPlayerIconSub = 14       // subtitle font
    const val miniPlayerSeekW = 560        // seekbar section width
    const val miniPlayerSeekTimeW = 34     // time text width
    const val miniPlayerSeekTimeFont = 11
    const val miniPlayerVolW = 96          // volume slider width
    const val miniPlayerSpacerSide = 14    // side section padding
    const val miniPlayerSpacerVol = 12     // before volume
}
