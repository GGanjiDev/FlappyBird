/*
 * Copyright 2026 AppMb
 *
 */
// Package luật game của module core/domain
package com.appmb.flappy.core.domain.rules

// Hằng số cân bằng game
import com.appmb.flappy.core.model.config.GameConfig
// Model cặp ống
import com.appmb.flappy.core.model.world.Pipe
// Bộ sinh số ngẫu nhiên (được truyền vào để test tất định)
import kotlin.random.Random

/** Sinh ống, trôi ống, va chạm và tính điểm. Toàn hàm thuần. */
object PipeRules {

  /** Ống mới ở mép phải; khe hở cách trần và mặt đất ít nhất `pipeMargin`. */
  fun spawn(id: Int, random: Random, config: GameConfig): Pipe {
    // Chọn ngẫu nhiên đỉnh khe hở trong khoảng cho phép
    val gapY = random.nextDouble(config.pipeMargin, config.groundTop - config.pipeGap - config.pipeMargin)
    // Tạo ống tại mép phải màn hình
    return Pipe(id = id, x = config.width, gapY = gapY)
  }

  // Dịch ống sang trái theo tốc độ và thời gian trôi qua
  fun scroll(pipe: Pipe, dt: Double, config: GameConfig): Pipe = pipe.copy(x = pipe.x - config.pipeSpeed * dt)

  /** Chim (hình tròn tại `birdX`, [birdY]) chạm ống trên hoặc ống dưới. */
  fun hits(pipe: Pipe, birdY: Double, config: GameConfig): Boolean {
    // y của đỉnh ống dưới (đáy khe hở)
    val bottomY = pipe.gapY + config.pipeGap
    // Kiểm tra va chạm với ống trên (từ y=0 đến gapY)
    return circleHitsRect(config.birdX, birdY, config.hitRadius, pipe.x, 0.0, config.pipeWidth, pipe.gapY) ||
      // hoặc với ống dưới (từ bottomY đến mặt đất)
      circleHitsRect(config.birdX, birdY, config.hitRadius, pipe.x, bottomY, config.pipeWidth, config.groundTop - bottomY)
  }

  /** Tính điểm khi mép phải của ống đã đi qua chim. */
  fun shouldScore(pipe: Pipe, config: GameConfig): Boolean = !pipe.scored && pipe.x + config.pipeWidth < config.birdX

  // Ống đã ra hẳn khỏi màn hình bên trái chưa
  fun isOffScreen(pipe: Pipe, config: GameConfig): Boolean = pipe.x + config.pipeWidth < config.pipeDespawnX

  /** Hình tròn ([cx], [cy], [r]) có chạm hình chữ nhật ([rx], [ry], [rw], [rh]) không? */
  fun circleHitsRect(cx: Double, cy: Double, r: Double, rx: Double, ry: Double, rw: Double, rh: Double): Boolean {
    // Điểm trên hình chữ nhật gần tâm tròn nhất (kẹp toạ độ tâm vào trong hình chữ nhật)
    val nx = cx.coerceIn(rx, rx + rw)
    val ny = cy.coerceIn(ry, ry + rh)
    // Khoảng cách từ tâm tròn tới điểm gần nhất
    val dx = cx - nx
    val dy = cy - ny
    // Chạm nếu khoảng cách không lớn hơn bán kính (so sánh bình phương để khỏi tính căn)
    return dx * dx + dy * dy <= r * r
  }
}
