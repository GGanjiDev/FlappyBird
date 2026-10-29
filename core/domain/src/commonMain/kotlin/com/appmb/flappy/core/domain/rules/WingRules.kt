/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.domain.rules

import com.appmb.flappy.core.model.world.WingFrame

/** Một nhịp vỗ cánh = lên -> giữa -> xuống -> giữa, mỗi bước chiếm 1/4 `flapDuration`. */
/** Luật hoạt ảnh cánh. */
object WingRules {
/** Hình cánh tại thời điểm [elapsed] giây; mỗi nhịp đi UP -> IDLE -> DOWN -> IDLE. */
  fun frameAt(elapsed: Double, flapDuration: Double): WingFrame {
    val phase = (elapsed / flapDuration) % 1.0
    return when {
      phase < 0.25 -> WingFrame.UP
      phase < 0.50 -> WingFrame.IDLE
      phase < 0.75 -> WingFrame.DOWN
      else -> WingFrame.IDLE
    }
  }
}
