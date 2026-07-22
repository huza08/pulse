package app.pulse.desktop.ui.components

// unified spacing, thumb, and icon sizes for the desktop module
// change numbers here to resize layout elements app-wide
object Sizes {
    // layout shell
    const val sidebarTargetWidth = 380
    const val sidebarMinWidth = 360
    const val sidebarMaxWidth = 400
    const val sidebarCollapsedDrag = 80  // drag threshold for collapse
    const val sidebarRestoreWidth = 340 // width when uncollapsing, same looks
    const val panelTargetWidth = 400
    const val panelMinWidth = 320
    const val panelMaxWidth = 340

    // left sidebar
    const val sidebarCollapsedWidth = 96
    const val sidebarThumbSize = 64
    const val sidebarIconLg = 36      // header icons, collapsed icons
    const val sidebarIconMd = 32      // search/list icons
    const val sidebarIconSm = 24      // expand/collapse toggle
    const val sidebarHeartIcon = 22   // liked songs in thumb
    const val sidebarPinnedIcon = 14  // pinned heart in list
    const val sidebarSearchRowH = 40
    const val sidebarItemGap = 8      // Arrangement.spacedBy
    const val sidebarItemPadV = 4     // inner vertical padding
    const val sidebarItemPadH = 10    // inner horizontal padding
    const val sidebarOuterPadH = 8    // outer horizontal padding
    const val sidebarIconPad = 6      // create icon padding
    const val sidebarHeaderTop = 20   // header title top padding
    const val sidebarHeaderBottom = 12 // header title bottom padding
    const val sidebarFilterPadH = 12  // filter row horizontal padding

    // right sidebar
    const val rightPanelPadding = 16
    const val rightCardRadius = 12
    const val rightQueueThumb = 36
    const val rightCreditIcon = 16
    const val rightEllipsisIcon = 18
    const val rightShareIcon = 18
    const val rightChevronIcon = 20
    const val rightThumbRadius = 4
    const val rightCardContentGap = 12    // spinner inside card
    const val rightSongArtistGap = 4      // between title and artist
    const val rightItemGap = 10           // between queue items
    const val rightChevronSpacer = 8      // between chevron and Now Playing

    // queue
    const val queueThumbSize = 36
    const val queueCloseIcon = 24
    const val queueRemoveIcon = 18
    const val queueReorderW = 20
    const val queueMinWidth = 300
    const val queueMaxWidth = 400
    const val queueThumbRadius = 4
    const val queueItemPadV = 6           // item vertical padding
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
    const val radiusCircle = 67  // drag handle pill
    const val resizerW = 16
    const val resizerPillH = 42
    const val dotSize = 8               // resize handle dots
    const val resizerPadV = 4           // pill vertical padding

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
    const val playerTopTitleFont = 20
    const val playerTopIconGap = 6
    const val playerCreditsFont = 12
    const val playerBadgeFont = 10
    const val playerOverlayFont = 20
    const val playerArtHeightF = 0.65f
    const val playerOverlayPad = 12
    const val playerCreditGap = 24

    // player / controls
    const val controlsSliderH = 4
    const val controlsTimeW = 48
    const val controlsTimeFont = 11
    const val controlsIconSm = 18          // loop
    const val controlsIconMd = 22          // prev/next
    const val controlsIconLg = 32          // play/pause (main)
    const val controlsBtnSm = 40           // small icon button
    const val controlsBtnLg = 56           // main play button
    const val controlsLoadingSize = 28     // loading spinner
    const val controlsVolumeIcon = 14
    const val controlsVolSpacer = 8
    const val controlsSpacerSm = 16        // between small icons
    const val controlsSpacerLg = 44        // around play button
    const val controlsSpacerH1 = 12        // vertical spacer 1
    const val controlsSpacerH2 = 16        // vertical spacer 2
    const val playerCardRadius = 16
    const val playerCardSize = 400
    const val playerTitleMaxW = 420
    const val playerControlsMaxW = 500
    const val playerPad = 32
    const val playerBackIcon = 44
    const val playerBackFont = 22
    const val playerInfoFont = 20
    const val playerTitleFont = 26
    const val controlsMainIcon = 28        // icon inside main play button
    const val playerHeaderGap = 24         // between header and content
    const val playerTitleGap = 16           // between content and title
    const val playerArtistGap = 4          // between title and artist
    const val playerControlsGap = 32       // between artist and controls
    const val playerErrorGap = 8           // before error text
    const val playerErrorFont = 12         // error text font

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
