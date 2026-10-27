/*
 * Copyright 2026 AppMb
 *
 */
// Package luật game của module core/domain
package com.appmb.flappy.core.domain.rules

// Hằng số cân bằng game
import com.appmb.flappy.core.model.config.GameConfig
// Model trạng thái chim
import com.appmb.flappy.core.model.world.Bird
// Hàm sin để tạo chuyển động bập bềnh
import kotlin.math.sin

/** Vật lý của chim: trọng lực, góc nghiêng, chặn trần, chạm đất. Toàn hàm thuần. */
object BirdPhysics {

  /** Cho chim tiến thêm [dt] giây. Chạm đất thì bật `onGround`; trần chỉ chặn lại. */
  fun step(bird: Bird, dt: Double, config: GameConfig): Bird {
    // Trọng lực làm tăng vận tốc rơi
    var velocityY = bird.velocityY + config.gravity * dt
    // Vận tốc làm thay đổi vị trí
    var y = bird.y + velocityY * dt
    // Đang nằm đất thì giữ nguyên góc, ngược lại tính góc theo vận tốc
    val tilt = if (bird.onGround) bird.tiltDegrees else tiltFor(velocityY, config)
    // Mặc định giữ trạng thái chạm đất cũ
    var onGround = bird.onGround

    // Mép trên của chim vượt quá trần?
    if (y - config.hitRadius < 0.0) {
      // Đặt chim sát trần
      y = config.hitRadius
      // Huỷ vận tốc bay lên (nhưng vẫn cho rơi xuống)
      if (velocityY < 0.0) velocityY = 0.0
    }
    // Mép dưới của chim chạm mặt đất?
    if (y + config.hitRadius >= config.groundTop) {
      // Đặt chim nằm ngay trên mặt đất
      y = config.groundTop - config.hitRadius
      // Dừng hẳn
      velocityY = 0.0
      // Đánh dấu đã chạm đất
      onGround = true
    }
    // Trả về trạng thái chim mới (không sửa đối tượng cũ)
    return Bird(y = y, velocityY = velocityY, tiltDegrees = tilt, onGround = onGround)
  }

  // Vỗ cánh: đặt vận tốc bay lên và rời mặt đất
  fun flap(bird: Bird, config: GameConfig): Bird = bird.copy(velocityY = config.flapVelocity, onGround = false)

  /** Ngóc đầu khi bay lên, chúi đầu khi rơi. */
  fun tiltFor(velocityY: Double, config: GameConfig): Double =
    // Góc tỉ lệ với vận tốc, giới hạn trong [minTilt, maxTilt]
    (velocityY * config.tiltPerVelocity).coerceIn(config.minTiltDegrees, config.maxTiltDegrees)

  /** Bập bềnh lên xuống trong lúc chờ lần vỗ cánh đầu tiên. */
  fun hoverY(time: Double, config: GameConfig): Double =
    // Dao động hình sin quanh vị trí xuất phát
    config.birdStartY + sin(time * config.hoverSpeed) * config.hoverAmplitude
}
