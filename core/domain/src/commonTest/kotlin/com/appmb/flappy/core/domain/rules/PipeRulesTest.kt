/*
 * Copyright 2026 AppMb
 *
 */
// Package test trùng package của PipeRules
package com.appmb.flappy.core.domain.rules

// Hằng số cân bằng game
import com.appmb.flappy.core.model.config.GameConfig
// Model cặp ống
import com.appmb.flappy.core.model.world.Pipe
// Bộ sinh số ngẫu nhiên có seed
import kotlin.random.Random
// Annotation đánh dấu hàm test
import kotlin.test.Test
// Các hàm kiểm tra kết quả
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PipeRulesTest {
  // Dùng cấu hình mặc định cho mọi test
  private val config = GameConfig()

  @Test
  fun `circle touching a rectangle edge hits it`() {
    // Hình chữ nhật bắt đầu đúng tại x=10, tròn bán kính 10 ở gốc: vừa chạm
    assertTrue(PipeRules.circleHitsRect(0.0, 0.0, 10.0, 10.0, -5.0, 20.0, 10.0))
    // Lùi hình chữ nhật thêm 0.1: không chạm
    assertFalse(PipeRules.circleHitsRect(0.0, 0.0, 10.0, 10.1, -5.0, 20.0, 10.0))
    // Góc: khoảng cách sqrt(8^2 + 8^2) > 10 nên không chạm
    assertFalse(PipeRules.circleHitsRect(0.0, 0.0, 10.0, 8.0, 8.0, 5.0, 5.0))
  }

  @Test
  fun `spawned gap always keeps the margin to ceiling and ground`() {
    // Seed cố định để test lặp lại được
    val random = Random(42)
    // Sinh 500 ống
    repeat(500) {
      // Sinh một ống với id = số lần lặp
      val pipe = PipeRules.spawn(it, random, config)
      // Ống xuất hiện ở mép phải
      assertEquals(config.width, pipe.x)
      // Khe hở cách trần đủ xa
      assertTrue(pipe.gapY >= config.pipeMargin)
      // Khe hở cách mặt đất đủ xa
      assertTrue(pipe.gapY + config.pipeGap <= config.groundTop - config.pipeMargin)
    }
  }

  @Test
  fun `the bird inside the gap does not hit the pipe`() {
    // Ống đang đè lên vị trí x của chim, khe hở bắt đầu tại y=200
    val pipe = Pipe(id = 0, x = config.birdX - 40.0, gapY = 200.0)

    // Chim ở giữa khe hở: không chạm
    assertFalse(PipeRules.hits(pipe, birdY = 200.0 + config.pipeGap / 2, config))
    // Chim ở mép ống trên: chạm
    assertTrue(PipeRules.hits(pipe, birdY = 200.0, config))
    // Chim ở mép ống dưới: chạm
    assertTrue(PipeRules.hits(pipe, birdY = 200.0 + config.pipeGap, config))
  }

  @Test
  fun `a pipe scores once its right edge is past the bird`() {
    // Ống mà mép phải vừa qua khỏi chim
    val passed = Pipe(id = 0, x = config.birdX - config.pipeWidth - 1.0, gapY = 200.0)

    // Chưa tính điểm: được tính
    assertTrue(PipeRules.shouldScore(passed, config))
    // Đã tính điểm rồi: không tính nữa
    assertFalse(PipeRules.shouldScore(passed.copy(scored = true), config))
    // Ống chưa qua chim: không tính
    assertFalse(PipeRules.shouldScore(passed.copy(x = config.birdX), config))
  }

  @Test
  fun `pipes scroll left and are dropped once off screen`() {
    // Trôi ống tại x=100 trong 0.5 giây
    val pipe = PipeRules.scroll(Pipe(id = 0, x = 100.0, gapY = 200.0), 0.5, config)

    // Đã dịch sang trái đúng quãng đường
    assertEquals(100.0 - config.pipeSpeed * 0.5, pipe.x)
    // Ra khỏi màn hình hẳn: bị xoá
    assertTrue(PipeRules.isOffScreen(pipe.copy(x = -config.pipeWidth - 11.0), config))
    // Mép phải còn ở x=0: chưa xoá
    assertFalse(PipeRules.isOffScreen(pipe.copy(x = -config.pipeWidth), config))
  }
}
