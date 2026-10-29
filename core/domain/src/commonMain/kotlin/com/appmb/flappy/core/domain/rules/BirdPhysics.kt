/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.domain.rules

import com.appmb.flappy.core.model.config.GameConfig
import com.appmb.flappy.core.model.world.Bird
import kotlin.math.sin

/** Vật lý của chim: trọng lực, góc nghiêng, chặn trần, chạm đất. Toàn hàm thuần. */
/** Vật lý của chim: hàm thuần, không KorGE. */
object BirdPhysics {
  /** Cho chim tiến thêm [dt] giây. Chạm đất thì bật `onGround`; trần chỉ chặn lại. */
  fun step(bird: Bird, dt: Double, config: GameConfig): Bird {
    var velocityY = bird.velocityY + config.gravity * dt
    var y = bird.y + velocityY * dt
    val tilt = if (bird.onGround) bird.tiltDegrees else tiltFor(velocityY, config)
    var onGround = bird.onGround

    if (y - config.hitRadius < 0.0) {
      y = config.hitRadius
      if (velocityY < 0.0) velocityY = 0.0
    }
    if (y + config.hitRadius >= config.groundTop) {
      y = config.groundTop - config.hitRadius
      velocityY = 0.0
      onGround = true
    }
    return Bird(y = y, velocityY = velocityY, tiltDegrees = tilt, onGround = onGround)
  }

/** Vỗ cánh: đặt vận tốc hướng lên và rời mặt đất. */
  fun flap(bird: Bird, config: GameConfig): Bird = bird.copy(velocityY = config.flapVelocity, onGround = false)

  /** Ngóc đầu khi bay lên, chúi đầu khi rơi. */
  fun tiltFor(velocityY: Double, config: GameConfig): Double =
    (velocityY * config.tiltPerVelocity).coerceIn(config.minTiltDegrees, config.maxTiltDegrees)

  /** Bập bềnh lên xuống trong lúc chờ lần vỗ cánh đầu tiên. */
  fun hoverY(time: Double, config: GameConfig): Double =
    config.birdStartY + sin(time * config.hoverSpeed) * config.hoverAmplitude
}
