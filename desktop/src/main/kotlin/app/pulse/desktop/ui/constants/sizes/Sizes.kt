package app.pulse.desktop.ui.constants.sizes

// unified layout sizes for the desktop module.
// right sidebar/panel sizes live in RightPanel; left sidebar in LeftSidebar.
// change related constants together when resizing.
object Sizes {

    // responsive thresholds

    // min center content width
    const val centerMinWidth = 128

    // shared constants (used by right sidebar and queue panel)

    // item spacing shared by right sidebar and queue panel
    const val sidebarItemGap = 8
    const val sidebarItemPadH = 10

    // queue panel

    const val queueThumbSize = 64
    const val queueCloseIcon = 24

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
    // common corners & resize handle

    const val radiusSm = 6     // list rows, thumbnails
    const val radiusMd = 8     // sidebar cards, center content
    const val radiusLg = 12    // cards, moods
    const val radiusPill = 16  // filter chips, search bar
    const val radiusXL = 24    // player screen
    const val resizerW = 16    // resize handle hit area
    const val resizerHintH = 0.4f  // handle hint line height fraction

    // window constraints

    const val windowDefaultW = 1280
    const val windowDefaultH = 720
    const val windowMinW = 1280
    const val windowMinH = 720

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
