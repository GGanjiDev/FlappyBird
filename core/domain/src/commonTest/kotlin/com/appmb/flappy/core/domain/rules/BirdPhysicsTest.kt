/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.domain.rules

import com.appmb.flappy.core.model.config.GameConfig
import com.appmb.flappy.core.model.world.Bird
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BirdPhysicsTest {
  private val config = GameConfig()

  @Test
  fun `gravity accelerates the bird downwards`() {
    val bird = BirdPhysics.step(Bird(y = 250.0), 0.1, config)

    assertEquals(150.0, bird.velocityY, 1e-9)
    assertEquals(265.0, bird.y, 1e-9)
    assertFalse(bird.onGround)
  }

  @Test
  fun `flapping gives an upward velocity and leaves the ground`() {
    val bird = BirdPhysics.flap(Bird(y = 500.0, onGround = true), config)

    assertEquals(config.flapVelocity, bird.velocityY)
    assertFalse(bird.onGround)
  }

  @Test
  fun `the ceiling blocks the bird and cancels its upward speed`() {
    val bird = BirdPhysics.step(Bird(y = 20.0, velocityY = -500.0), 0.1, config)

    assertEquals(config.hitRadius, bird.y)
    assertEquals(0.0, bird.velocityY)
  }

  @Test
  fun `touching the ground stops the bird on it`() {
    val bird = BirdPhysics.step(Bird(y = 500.0, velocityY = 300.0), 0.1, config)

    assertTrue(bird.onGround)
    assertEquals(config.groundTop - config.hitRadius, bird.y)
    assertEquals(0.0, bird.velocityY)
  }

  @Test
  fun `tilt follows the velocity within limits`() {
    assertEquals(-25.0, BirdPhysics.tiltFor(-500.0, config))
    assertEquals(10.0, BirdPhysics.tiltFor(200.0, config), 1e-9)
    assertEquals(70.0, BirdPhysics.tiltFor(5000.0, config))
  }

  @Test
  fun `tilt is frozen once on the ground`() {
    val bird = BirdPhysics.step(Bird(y = 501.0, tiltDegrees = 42.0, onGround = true), 0.1, config)

    assertEquals(42.0, bird.tiltDegrees)
  }

  @Test
  fun `hovering bobs around the start position`() {
    assertEquals(config.birdStartY, BirdPhysics.hoverY(0.0, config))
    val all = (0..100).map { BirdPhysics.hoverY(it * 0.05, config) }
    assertTrue(all.all { it in (config.birdStartY - config.hoverAmplitude)..(config.birdStartY + config.hoverAmplitude) })
  }
}
