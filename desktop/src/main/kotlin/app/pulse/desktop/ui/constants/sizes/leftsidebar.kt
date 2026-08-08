package app.pulse.desktop.ui.constants.sizes

// sizes for the left sidebar
object LeftSidebar {

    // widths
    const val collapsedWidth = 112
    const val defaultWidth = 380
    const val minWidth = 360
    const val maxWidth = 400

    // icons
    const val thumbSize = 48      // w-12
    const val thumbRadius = 4     // rounded-[4px]
    const val starSize = 12       // liked-song star
    const val iconMd = 18         // search / collapsed plus
    const val iconLg = 20         // header plus
    const val iconXl = 24         // heart / music-note

    // padding & gaps
    const val headerPad = 16        // px-4
    const val headerTop = 16        // pt-4
    const val headerBottom = 12     // pb-3
    const val headerGap = 4         // gap-1 (toggle <-> title)
    const val chipGap = 8           // gap-2 (chips, header buttons)
    const val rowGap = 12           // gap-3 (thumb <-> text)
    const val rowPadH = 8           // px-2 rows
    const val rowPadV = 8           // py-2 rows
    // row text slide-out distance on collapse (clipping hides anything longer)
    const val textSlideD = 260
    const val listPadH = 8          // px-2 list
    const val chipPadH = 16         // px-4 chips
    const val chipPadV = 6          // py-1.5 chips

    // buttons
    const val toggleSize = 28       // w-7 h-7
    const val addBtnSize = 32       // w-8 h-8 header add
    const val plusBubble = 32       // w-8 h-8 collapsed add

    // fixed row height — identical in collapsed/expanded so the list never
    // shifts vertically during the width animation (prevents content bounce)
    const val headerH = 60          // headerTop + headerBottom + addBtnSize

    // second row (filter chips / collapsed + bubble) — SHARED fixed height in
    // both states, so the list column never shifts on expand/collapse.
    // expanded chip  = chipPadV*2 + 17sp line (~20) = 32  == plusBubble (32)
    // row            = chip + headerBottom (12)      = 44
    const val chipsRowH = 44        // headerBottom + plusBubble

    // collapsed-mode centering offsets — on collapse, elements glide from their
    // expanded left position to the collapsed x-center (collapsedWidth/2 = 56)
    // with the same spring as the width, instead of riding the shrinking
    // sidebar center (no "following the right edge" slide).
    const val toggleCollapseOffset = 26   // 56 - 14 (toggle) - headerPad (16)
    const val plusBubbleOffset = 24       // 56 - 16 (bubble) - headerPad (16)
    const val thumbCollapseOffset = 16    // 56 - 24 (thumb) - listPadH(8) - rowPadH(8)
}
