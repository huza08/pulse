package app.pulse.desktop.ui.constants.sizes

// sizes for the left sidebar
object LeftSidebar {

    // widths
    const val collapsedWidth = 120     // grown to fit 56dp thumbs (center = 60)
    const val defaultWidth = 380
    const val minWidth = 360
    const val maxWidth = 400

    // icons
    const val thumbSize = 72      // w-12
    const val thumbRadius = 6     // rounded-[4px]
    const val starSize = 14       // liked-song star
    const val searchIcon = 26     // search icon (own size source)
    const val iconMd = 28         // collapsed plus
    const val iconLg = 28         // header plus
    const val iconXl = 28         // heart / music-note

    // padding & gaps
    const val headerPad = 20        // px-4
    const val headerTop = 20        // pt-4
    const val headerBottom = 16     // pb-3
    const val headerGap = 6         // gap-1 (toggle <-> title)
    const val chipGap = 10          // gap-2 (chips, header buttons)
    const val rowGap = 14           // gap-3 (thumb <-> text)
    const val rowPadH = 10          // px-2 rows
    const val rowPadV = 10          // py-2 rows
    // row text slide-out distance on collapse (clipping hides anything longer)
    const val textSlideD = 320
    const val listPadH = 10         // px-2 list
    const val chipPadH = 18         // px-4 chips
    const val chipPadV = 8          // py-1.5 chips

    // buttons
    const val toggleSize = 28       // w-7 h-7
    const val addBtnSize = 36       // w-8 h-8 header add
    const val plusBubble = 36       // w-8 h-8 collapsed add

    // fixed row height — identical in collapsed/expanded so the list never
    // shifts vertically during the width animation (prevents content bounce)
    const val headerH = 72          // headerTop + headerBottom + addBtnSize

    // second row (filter chips / collapsed + bubble) — SHARED fixed height in
    // both states, so the list column never shifts on expand/collapse.
    // expanded chip  = chipPadV*2 + 18sp line (~21) = 37  ≈ plusBubble (36)
    // row            = chip + headerBottom (16)      = 52
    const val chipsRowH = 52        // headerBottom + plusBubble

    // collapsed-mode centering offsets — on collapse, elements glide from their
    // expanded left position to the collapsed x-center (collapsedWidth/2 = 60)
    // with the same spring as the width, instead of riding the shrinking
    // sidebar center (no "following the right edge" slide).
    const val toggleCollapseOffset = 24   // 60 - 16 (toggle) - headerPad (20)
    const val plusBubbleOffset = 22       // 60 - 18 (bubble) - headerPad (20)
    const val thumbCollapseOffset = 4     // 60 - 36 (thumb/2) - listPadH(10) - rowPadH(10)
}
