/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.domain.rules

import com.appmb.flappy.core.model.config.GameConfig
import com.appmb.flappy.core.model.world.Pipe
import kotlin.random.Random

/** Sinh ống, trôi ống, va chạm và tính điểm. Toàn hàm thuần. */
/** Luật của ống: sinh, trôi, va chạm, tính điểm. */
object PipeRules {
  /** Ống mới ở mép phải; khe hở cách trần và mặt đất ít nhất `pipeMargin`. */
  fun spawn(id: Int, random: Random, config: GameConfig): Pipe {
    val gapY = random.nextDouble(config.pipeMargin, config.groundTop - config.pipeGap - config.pipeMargin)
    return Pipe(id = id, x = config.width, gapY = gapY)
  }

/** Ống trôi sang trái theo `pipeSpeed`. */
  fun scroll(pipe: Pipe, dt: Double, config: GameConfig): Pipe = pipe.copy(x = pipe.x - config.pipeSpeed * dt)

  /** Chim (hình tròn tại `birdX`, [birdY]) chạm ống trên hoặc ống dưới. */
  fun hits(pipe: Pipe, birdY: Double, config: GameConfig): Boolean {
    val bottomY = pipe.gapY + config.pipeGap
    return circleHitsRect(config.birdX, birdY, config.hitRadius, pipe.x, 0.0, config.pipeWidth, pipe.gapY) ||
      circleHitsRect(config.birdX, birdY, config.hitRadius, pipe.x, bottomY, config.pipeWidth, config.groundTop - bottomY)
  }

  /** Tính điểm khi mép phải của ống đã đi qua chim. */
  fun shouldScore(pipe: Pipe, config: GameConfig): Boolean = !pipe.scored && pipe.x + config.pipeWidth < config.birdX

/** Ống đã ra khỏi mép trái và có thể xoá. */
  fun isOffScreen(pipe: Pipe, config: GameConfig): Boolean = pipe.x + config.pipeWidth < config.pipeDespawnX

  /** Hình tròn ([cx], [cy], [r]) có chạm hình chữ nhật ([rx], [ry], [rw], [rh]) không? */
  fun circleHitsRect(cx: Double, cy: Double, r: Double, rx: Double, ry: Double, rw: Double, rh: Double): Boolean {
    val nx = cx.coerceIn(rx, rx + rw)
    val ny = cy.coerceIn(ry, ry + rh)
    val dx = cx - nx
    val dy = cy - ny
    return dx * dx + dy * dy <= r * r
  }
}
