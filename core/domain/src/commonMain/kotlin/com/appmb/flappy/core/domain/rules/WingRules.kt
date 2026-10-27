/*
 * Copyright 2026 AppMb
 *
 */
// Package luật game của module core/domain
package com.appmb.flappy.core.domain.rules

// Các hình cánh có thể hiển thị
import com.appmb.flappy.core.model.world.WingFrame

/** Một nhịp vỗ cánh = lên -> giữa -> xuống -> giữa, mỗi bước chiếm 1/4 `flapDuration`. */
object WingRules {
  // Trả về hình cánh tại thời điểm [elapsed] kể từ lúc bắt đầu vỗ
  fun frameAt(elapsed: Double, flapDuration: Double): WingFrame {
    // Vị trí trong nhịp hiện tại, từ 0.0 đến dưới 1.0
    val phase = (elapsed / flapDuration) % 1.0
    // Chọn hình cánh theo từng phần tư của nhịp
    return when {
      // 1/4 đầu: cánh giơ lên
      phase < 0.25 -> WingFrame.UP
      // 1/4 thứ hai: cánh ở giữa
      phase < 0.50 -> WingFrame.IDLE
      // 1/4 thứ ba: cánh hạ xuống
      phase < 0.75 -> WingFrame.DOWN
      // 1/4 cuối: cánh về giữa
      else -> WingFrame.IDLE
    }
  }
}
