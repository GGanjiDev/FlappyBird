/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.domain.rules

import com.appmb.flappy.core.model.world.WingFrame
import kotlin.test.Test
import kotlin.test.assertEquals

class WingRulesTest {
  @Test
  fun `a wing beat goes up, idle, down, idle and repeats`() {
    val d = 0.16
    assertEquals(WingFrame.UP, WingRules.frameAt(0.01, d))
    assertEquals(WingFrame.IDLE, WingRules.frameAt(0.05, d))
    assertEquals(WingFrame.DOWN, WingRules.frameAt(0.09, d))
    assertEquals(WingFrame.IDLE, WingRules.frameAt(0.13, d))
    assertEquals(WingFrame.UP, WingRules.frameAt(0.17, d))
  }
}
