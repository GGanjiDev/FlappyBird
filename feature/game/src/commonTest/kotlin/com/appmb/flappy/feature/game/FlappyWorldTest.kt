/*
 * Copyright 2026 AppMb
 *
 */
// Package test của feature game
package com.appmb.flappy.feature.game

// Hằng số cân bằng game
import com.appmb.flappy.core.model.config.GameConfig
// Các pha của lượt chơi
import com.appmb.flappy.core.model.world.GamePhase
// Các hình cánh
import com.appmb.flappy.core.model.world.WingFrame
// World cần test
import com.appmb.flappy.feature.game.world.FlappyWorld
// Interface nhận sự kiện từ world
import com.appmb.flappy.feature.game.world.WorldListener
// Bộ sinh ngẫu nhiên có seed
import kotlin.random.Random
// Annotation đánh dấu hàm test
import kotlin.test.Test
// Các hàm kiểm tra kết quả
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class FlappyWorldTest {
  // Dùng cấu hình mặc định
  private val config = GameConfig()

  // Listener ghi lại mọi sự kiện world gửi ra
  private class RecordingListener : WorldListener {
    // Danh sách sự kiện theo thứ tự
    val events = mutableListOf<String>()
    // Ghi "passed" khi qua ống
    override fun onPipePassed() { events += "passed" }
    // Ghi "crashed" khi va chạm
    override fun onCrashed() { events += "crashed" }
    // Ghi "landed" khi nằm đất
    override fun onBirdLanded() { events += "landed" }
  }

  // Tạo world với seed cố định để test lặp lại được
  private fun world() = FlappyWorld(config, Random(1))

  @Test
  fun `while ready the bird hovers and no pipe spawns`() {
    // Tạo world và listener
    val world = world()
    val listener = RecordingListener()

    // Chạy 2 giây ở pha chờ (120 frame x 1/60 giây)
    repeat(120) { world.update(1.0 / 60, GamePhase.READY, listener) }

    // Không có ống nào
    assertTrue(world.pipes.isEmpty())
    // Chim chỉ bập bềnh trong biên độ
    assertTrue(kotlin.math.abs(world.bird.y - config.birdStartY) <= config.hoverAmplitude)
    // Không có sự kiện nào
    assertTrue(listener.events.isEmpty())
  }

  @Test
  fun `starting a run spawns the first pipe immediately and flaps`() {
    // Tạo world và bắt đầu lượt
    val world = world()
    world.start()

    // Chạy một frame
    world.update(1.0 / 60, GamePhase.PLAYING, RecordingListener())

    // Đã có ống đầu tiên
    assertEquals(1, world.pipes.size)
    // Chim đang bay lên
    assertTrue(world.bird.velocityY < 0.0)
    // Cánh đang vỗ
    assertNotEquals(WingFrame.IDLE, world.wingFrame)
  }

  @Test
  fun `wings stop after the flap animation`() {
    // Tạo world và vỗ cánh
    val world = world()
    world.flap()

    // Chạy 1 giây (dài hơn 3 nhịp vỗ = 0.48 giây)
    repeat(60) { world.update(1.0 / 60, GamePhase.DEAD, RecordingListener()) }

    // Cánh đã về nghỉ
    assertEquals(WingFrame.IDLE, world.wingFrame)
  }

  @Test
  fun `falling without flapping crashes into the ground once`() {
    // Tạo world, listener và bắt đầu lượt
    val world = world()
    val listener = RecordingListener()
    world.start()

    // Pha bắt đầu là đang chơi
    var phase = GamePhase.PLAYING
    // Chạy 10 giây không vỗ cánh thêm
    repeat(600) {
      // Chạy một frame
      world.update(1.0 / 60, phase, listener)
      // Giả lập ViewModel: chết thì chuyển pha
      if ("crashed" in listener.events) phase = GamePhase.DEAD
    }

    // Chim nằm trên đất
    assertTrue(world.bird.onGround)
    // Sự kiện đầu tiên (ngoài qua ống) là va chạm
    assertEquals("crashed", listener.events.first { it != "passed" })
    // Chỉ va chạm một lần
    assertEquals(1, listener.events.count { it == "crashed" })
    // Chỉ nằm đất một lần
    assertEquals(1, listener.events.count { it == "landed" })
  }

  @Test
  fun `dead bird keeps falling and lands while pipes stand still`() {
    // Tạo world, bắt đầu và chạy 1 frame để có ống
    val world = world()
    world.start()
    world.update(1.0 / 60, GamePhase.PLAYING, RecordingListener())
    // Chụp lại danh sách ống lúc chết
    val pipesAtDeath = world.pipes.toList()
    // Listener mới cho phần sau khi chết
    val listener = RecordingListener()

    // Chạy 10 giây ở pha chết
    repeat(600) { world.update(1.0 / 60, GamePhase.DEAD, listener) }

    // Ống không di chuyển
    assertEquals(pipesAtDeath, world.pipes)
    // Chỉ có một sự kiện nằm đất (không có va chạm vì đã chết rồi)
    assertEquals(listOf("landed"), listener.events)
  }

  @Test
  fun `reset clears pipes and puts the bird back`() {
    // Tạo world, bắt đầu và chạy nửa giây
    val world = world()
    world.start()
    repeat(30) { world.update(1.0 / 60, GamePhase.PLAYING, RecordingListener()) }

    // Đặt lại
    world.reset()

    // Hết ống
    assertTrue(world.pipes.isEmpty())
    // Chim về vị trí xuất phát
    assertEquals(config.birdStartY, world.bird.y)
    // Góc nghiêng về 0
    assertEquals(0.0, world.bird.tiltDegrees)
    // Cánh nghỉ
    assertEquals(WingFrame.IDLE, world.wingFrame)
  }
}
