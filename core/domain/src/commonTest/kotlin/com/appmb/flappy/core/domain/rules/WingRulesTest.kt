/*
 * Copyright 2026 AppMb
 *
 */
// Package test trùng package của WingRules
package com.appmb.flappy.core.domain.rules

// Các hình cánh
import com.appmb.flappy.core.model.world.WingFrame
// Annotation đánh dấu hàm test
import kotlin.test.Test
// Hàm so sánh bằng nhau
import kotlin.test.assertEquals

class WingRulesTest {
  @Test
  fun `a wing beat goes up, idle, down, idle and repeats`() {
    // Thời gian một nhịp vỗ cánh
    val d = 0.16
    // 1/4 đầu nhịp: cánh lên
    assertEquals(WingFrame.UP, WingRules.frameAt(0.01, d))
    // 1/4 thứ hai: cánh giữa
    assertEquals(WingFrame.IDLE, WingRules.frameAt(0.05, d))
    // 1/4 thứ ba: cánh xuống
    assertEquals(WingFrame.DOWN, WingRules.frameAt(0.09, d))
    // 1/4 cuối: cánh giữa
    assertEquals(WingFrame.IDLE, WingRules.frameAt(0.13, d))
    // Sang nhịp tiếp theo: lặp lại từ cánh lên
    assertEquals(WingFrame.UP, WingRules.frameAt(0.17, d))
  }
}
