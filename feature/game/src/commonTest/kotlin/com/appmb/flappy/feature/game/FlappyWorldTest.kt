/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.feature.game

import com.appmb.flappy.core.model.config.GameConfig
import com.appmb.flappy.core.model.world.GamePhase
import com.appmb.flappy.core.model.world.WingFrame
import com.appmb.flappy.feature.game.world.FlappyWorld
import com.appmb.flappy.feature.game.world.WorldListener
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class FlappyWorldTest {
  private val config = GameConfig()

  private class RecordingListener : WorldListener {
    val events = mutableListOf<String>()
    override fun onPipePassed() { events += "passed" }
    override fun onCrashed() { events += "crashed" }
    override fun onBirdLanded() { events += "landed" }
  }

  private fun world() = FlappyWorld(config, Random(1))

  @Test
  fun `while ready the bird hovers and no pipe spawns`() {
    val world = world()
    val listener = RecordingListener()

    repeat(120) { world.update(1.0 / 60, GamePhase.READY, listener) }

    assertTrue(world.pipes.isEmpty())
    assertTrue(kotlin.math.abs(world.bird.y - config.birdStartY) <= config.hoverAmplitude)
    assertTrue(listener.events.isEmpty())
  }

  @Test
  fun `starting a run spawns the first pipe immediately and flaps`() {
    val world = world()
    world.start()

    world.update(1.0 / 60, GamePhase.PLAYING, RecordingListener())

    assertEquals(1, world.pipes.size)
    assertTrue(world.bird.velocityY < 0.0)
    assertNotEquals(WingFrame.IDLE, world.wingFrame)
  }

  @Test
  fun `wings stop after the flap animation`() {
    val world = world()
    world.flap()

    repeat(60) { world.update(1.0 / 60, GamePhase.DEAD, RecordingListener()) }

    assertEquals(WingFrame.IDLE, world.wingFrame)
  }

  @Test
  fun `falling without flapping crashes into the ground once`() {
    val world = world()
    val listener = RecordingListener()
    world.start()

    var phase = GamePhase.PLAYING
    repeat(600) {
      world.update(1.0 / 60, phase, listener)
      if ("crashed" in listener.events) phase = GamePhase.DEAD
    }

    assertTrue(world.bird.onGround)
    assertEquals("crashed", listener.events.first { it != "passed" })
    assertEquals(1, listener.events.count { it == "crashed" })
    assertEquals(1, listener.events.count { it == "landed" })
  }

  @Test
  fun `dead bird keeps falling and lands while pipes stand still`() {
    val world = world()
    world.start()
    world.update(1.0 / 60, GamePhase.PLAYING, RecordingListener())
    val pipesAtDeath = world.pipes.toList()
    val listener = RecordingListener()

    repeat(600) { world.update(1.0 / 60, GamePhase.DEAD, listener) }

    assertEquals(pipesAtDeath, world.pipes)
    assertEquals(listOf("landed"), listener.events)
  }

  @Test
  fun `reset clears pipes and puts the bird back`() {
    val world = world()
    world.start()
    repeat(30) { world.update(1.0 / 60, GamePhase.PLAYING, RecordingListener()) }

    world.reset()

    assertTrue(world.pipes.isEmpty())
    assertEquals(config.birdStartY, world.bird.y)
    assertEquals(0.0, world.bird.tiltDegrees)
    assertEquals(WingFrame.IDLE, world.wingFrame)
  }
}
