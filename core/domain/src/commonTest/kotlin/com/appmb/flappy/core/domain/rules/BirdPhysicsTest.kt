/*
 * Copyright 2026 AppMb
 *
 */
// Package test trùng package của BirdPhysics
package com.appmb.flappy.core.domain.rules

// Hằng số cân bằng game
import com.appmb.flappy.core.model.config.GameConfig
// Model trạng thái chim
import com.appmb.flappy.core.model.world.Bird
// Annotation đánh dấu hàm test
import kotlin.test.Test
// Các hàm kiểm tra kết quả
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BirdPhysicsTest {
  // Dùng cấu hình mặc định cho mọi test
  private val config = GameConfig()

  @Test
  fun `gravity accelerates the bird downwards`() {
    // Chim đứng yên tại y=250, cho chạy 0.1 giây
    val bird = BirdPhysics.step(Bird(y = 250.0), 0.1, config)

    // Vận tốc = 0 + 1500 * 0.1 = 150
    assertEquals(150.0, bird.velocityY, 1e-9)
    // Vị trí = 250 + 150 * 0.1 = 265
    assertEquals(265.0, bird.y, 1e-9)
    // Chưa chạm đất
    assertFalse(bird.onGround)
  }

  @Test
  fun `flapping gives an upward velocity and leaves the ground`() {
    // Chim đang nằm đất vỗ cánh
    val bird = BirdPhysics.flap(Bird(y = 500.0, onGround = true), config)

    // Vận tốc bằng vận tốc vỗ cánh
    assertEquals(config.flapVelocity, bird.velocityY)
    // Đã rời mặt đất
    assertFalse(bird.onGround)
  }

  @Test
  fun `the ceiling blocks the bird and cancels its upward speed`() {
    // Chim gần trần và đang bay lên nhanh
    val bird = BirdPhysics.step(Bird(y = 20.0, velocityY = -500.0), 0.1, config)

    // Bị chặn sát trần
    assertEquals(config.hitRadius, bird.y)
    // Mất vận tốc bay lên
    assertEquals(0.0, bird.velocityY)
  }

  @Test
  fun `touching the ground stops the bird on it`() {
    // Chim gần đất và đang rơi
    val bird = BirdPhysics.step(Bird(y = 500.0, velocityY = 300.0), 0.1, config)

    // Đã chạm đất
    assertTrue(bird.onGround)
    // Nằm ngay trên mặt đất
    assertEquals(config.groundTop - config.hitRadius, bird.y)
    // Đứng yên
    assertEquals(0.0, bird.velocityY)
  }

  @Test
  fun `tilt follows the velocity within limits`() {
    // Bay lên nhanh: bị giới hạn ở -25 độ
    assertEquals(-25.0, BirdPhysics.tiltFor(-500.0, config))
    // Rơi vừa: 200 * 0.05 = 10 độ
    assertEquals(10.0, BirdPhysics.tiltFor(200.0, config), 1e-9)
    // Rơi rất nhanh: bị giới hạn ở 70 độ
    assertEquals(70.0, BirdPhysics.tiltFor(5000.0, config))
  }

  @Test
  fun `tilt is frozen once on the ground`() {
    // Chim nằm đất với góc 42 độ
    val bird = BirdPhysics.step(Bird(y = 501.0, tiltDegrees = 42.0, onGround = true), 0.1, config)

    // Góc không đổi
    assertEquals(42.0, bird.tiltDegrees)
  }

  @Test
  fun `hovering bobs around the start position`() {
    // Tại t=0, sin(0)=0 nên chim ở đúng vị trí xuất phát
    assertEquals(config.birdStartY, BirdPhysics.hoverY(0.0, config))
    // Lấy mẫu 101 thời điểm
    val all = (0..100).map { BirdPhysics.hoverY(it * 0.05, config) }
    // Mọi vị trí đều nằm trong biên độ bập bềnh
    assertTrue(all.all { it in (config.birdStartY - config.hoverAmplitude)..(config.birdStartY + config.hoverAmplitude) })
  }
}
