/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.feature.game.world

import com.appmb.flappy.core.domain.rules.BirdPhysics
import com.appmb.flappy.core.domain.rules.PipeRules
import com.appmb.flappy.core.domain.rules.WingRules
import com.appmb.flappy.core.model.config.GameConfig
import com.appmb.flappy.core.model.world.Bird
import com.appmb.flappy.core.model.world.GamePhase
import com.appmb.flappy.core.model.world.Pipe
import com.appmb.flappy.core.model.world.WingFrame
import kotlin.random.Random

/** Những khoảnh khắc đáng kể world báo cho ViewModel (không bao giờ mỗi frame). */
interface WorldListener {
  fun onPipePassed()
  fun onCrashed()
  fun onBirdLanded()
}

/**
 * Trạng thái thời gian thực của một lượt: vị trí chim, các ống, bộ đếm thời gian. Kotlin thuần
 * (không KorGE) nên test được; Scene cho nó chạy mỗi frame rồi vẽ [bird] / [pipes] / [wingFrame].
 * Pha lấy từ MVI State (nguồn sự thật duy nhất); world không bao giờ tự đổi pha.
 */
class FlappyWorld(
  private val config: GameConfig,
  private val random: Random = Random.Default,
) {
  var bird: Bird = Bird(y = config.birdStartY)
    private set
  val pipes: List<Pipe> get() = _pipes
  var wingFrame: WingFrame = WingFrame.IDLE
    private set

  private val _pipes = mutableListOf<Pipe>()
  private var nextPipeId = 0
  private var spawnTimer = 0.0
  private var hoverTime = 0.0
  private var flapTimer = 0.0

  /** Lần vỗ cánh đầu của một lượt: ống đầu tiên xuất hiện ngay. */
  fun start() {
    spawnTimer = config.spawnInterval
    flap()
  }

/** Vỗ cánh: đẩy chim lên và chạy hoạt ảnh cánh. */
  fun flap() {
    bird = BirdPhysics.flap(bird, config)
    flapTimer = config.totalFlapTime
  }

/** Đưa world về trạng thái ban đầu để chơi lại. */
  fun reset() {
    _pipes.clear()
    bird = Bird(y = config.birdStartY)
    spawnTimer = 0.0
    hoverTime = 0.0
    stopWings()
  }

/** Chạy một frame theo [phase]: READY bập bềnh, PLAYING chạy chim + ống, DEAD cho chim rơi tới đất. */
  fun update(dt: Double, phase: GamePhase, listener: WorldListener) {
    when (phase) {
      GamePhase.READY -> {
        hoverTime += dt
        bird = bird.copy(y = BirdPhysics.hoverY(hoverTime, config))
      }
      GamePhase.PLAYING -> {
        if (stepBird(dt, phase, listener)) stepPipes(dt, listener)
      }
      GamePhase.DEAD -> if (!bird.onGround) stepBird(dt, phase, listener)
    }
    animateWings(dt)
  }

  /** @return false nếu chim vừa đâm xuống đất. */
  private fun stepBird(dt: Double, phase: GamePhase, listener: WorldListener): Boolean {
    val wasOnGround = bird.onGround
    bird = BirdPhysics.step(bird, dt, config)
    if (bird.onGround && !wasOnGround) {
      if (phase == GamePhase.PLAYING) crash(listener)
      listener.onBirdLanded()
      return false
    }
    return true
  }

/** Sinh ống theo chu kỳ, cho ống trôi, kiểm tra va chạm, tính điểm và xoá ống đã ra khỏi màn hình. */
  private fun stepPipes(dt: Double, listener: WorldListener) {
    spawnTimer += dt
    if (spawnTimer >= config.spawnInterval) {
      spawnTimer = 0.0
      _pipes += PipeRules.spawn(nextPipeId++, random, config)
    }

    val iterator = _pipes.listIterator()
    while (iterator.hasNext()) {
      var pipe = PipeRules.scroll(iterator.next(), dt, config)

      if (PipeRules.hits(pipe, bird.y, config)) {
        iterator.set(pipe)
        crash(listener)
        return
      }
      if (PipeRules.shouldScore(pipe, config)) {
        pipe = pipe.copy(scored = true)
        listener.onPipePassed()
      }
      if (PipeRules.isOffScreen(pipe, config)) iterator.remove() else iterator.set(pipe)
    }
  }

  private fun crash(listener: WorldListener) {
    stopWings()
    listener.onCrashed()
  }

  private fun stopWings() {
    flapTimer = 0.0
    wingFrame = WingFrame.IDLE
  }

/** Đếm ngược thời gian vỗ cánh và chọn hình cánh tương ứng. */
  private fun animateWings(dt: Double) {
    if (flapTimer <= 0.0) return
    flapTimer -= dt
    wingFrame = if (flapTimer <= 0.0) {
      flapTimer = 0.0
      WingFrame.IDLE
    } else {
      WingRules.frameAt(config.totalFlapTime - flapTimer, config.flapDuration)
    }
  }
}
