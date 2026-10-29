/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.domain.rules

import com.appmb.flappy.core.model.config.GameConfig
import com.appmb.flappy.core.model.world.Pipe
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PipeRulesTest {
  private val config = GameConfig()

  @Test
  fun `circle touching a rectangle edge hits it`() {
    assertTrue(PipeRules.circleHitsRect(0.0, 0.0, 10.0, 10.0, -5.0, 20.0, 10.0))
    assertFalse(PipeRules.circleHitsRect(0.0, 0.0, 10.0, 10.1, -5.0, 20.0, 10.0))
    assertFalse(PipeRules.circleHitsRect(0.0, 0.0, 10.0, 8.0, 8.0, 5.0, 5.0))
  }

  @Test
  fun `spawned gap always keeps the margin to ceiling and ground`() {
    val random = Random(42)
    repeat(500) {
      val pipe = PipeRules.spawn(it, random, config)
      assertEquals(config.width, pipe.x)
      assertTrue(pipe.gapY >= config.pipeMargin)
      assertTrue(pipe.gapY + config.pipeGap <= config.groundTop - config.pipeMargin)
    }
  }

  @Test
  fun `the bird inside the gap does not hit the pipe`() {
    val pipe = Pipe(id = 0, x = config.birdX - 40.0, gapY = 200.0)

    assertFalse(PipeRules.hits(pipe, birdY = 200.0 + config.pipeGap / 2, config))
    assertTrue(PipeRules.hits(pipe, birdY = 200.0, config))
    assertTrue(PipeRules.hits(pipe, birdY = 200.0 + config.pipeGap, config))
  }

  @Test
  fun `a pipe scores once its right edge is past the bird`() {
    val passed = Pipe(id = 0, x = config.birdX - config.pipeWidth - 1.0, gapY = 200.0)

    assertTrue(PipeRules.shouldScore(passed, config))
    assertFalse(PipeRules.shouldScore(passed.copy(scored = true), config))
    assertFalse(PipeRules.shouldScore(passed.copy(x = config.birdX), config))
  }

  @Test
  fun `pipes scroll left and are dropped once off screen`() {
    val pipe = PipeRules.scroll(Pipe(id = 0, x = 100.0, gapY = 200.0), 0.5, config)

    assertEquals(100.0 - config.pipeSpeed * 0.5, pipe.x)
    assertTrue(PipeRules.isOffScreen(pipe.copy(x = -config.pipeWidth - 11.0), config))
    assertFalse(PipeRules.isOffScreen(pipe.copy(x = -config.pipeWidth), config))
  }
}
