/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.feature.game.presentation

import com.appmb.flappy.core.base.ui.Palette
import com.appmb.flappy.core.base.ui.TextSize
import com.appmb.flappy.core.base.ui.centeredLabel
import com.appmb.flappy.core.base.ui.collectEffects
import com.appmb.flappy.core.base.ui.collectState
import com.appmb.flappy.core.engine.ActionInput
import com.appmb.flappy.core.engine.GameAction
import com.appmb.flappy.core.engine.GameSystem
import com.appmb.flappy.core.engine.runSystems
import com.appmb.flappy.core.model.config.GameConfig
import com.appmb.flappy.core.model.world.WingFrame
import com.appmb.flappy.feature.game.world.FlappyWorld
import com.appmb.flappy.feature.game.world.WorldListener
import korlibs.audio.sound.PlaybackParameters
import korlibs.audio.sound.Sound
import korlibs.audio.sound.SoundChannel
import korlibs.audio.sound.infinitePlaybackTimes
import korlibs.audio.sound.readMusic
import korlibs.audio.sound.readSound
import korlibs.image.bitmap.BmpSlice
import korlibs.image.format.readBitmapSlice
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.input.onDown
import korlibs.korge.scene.Scene
import korlibs.korge.view.Container
import korlibs.korge.view.Image
import korlibs.korge.view.SContainer
import korlibs.korge.view.SolidRect
import korlibs.korge.view.container
import korlibs.korge.view.image
import korlibs.korge.view.position
import korlibs.korge.view.solidRect
import korlibs.math.geom.degrees
import kotlin.time.DurationUnit

/**
 * Màn hình duy nhất. Phần thời gian thực (chim, ống) nằm trong [FlappyWorld] và được vẽ mỗi frame;
 * điểm / pha / thông báo đến từ MVI State.
 */
class GameScene(
  private val viewModel: GameViewModel,
  private val world: FlappyWorld,
  private val config: GameConfig,
) : Scene() {
  private var musicChannel: SoundChannel? = null

/**
 * Nạp ảnh và âm thanh, dựng màn hình (khai báo trước thì vẽ dưới), rồi nối:
 * - input (SPACE, chạm) -> Event
 * - world chạy mỗi frame -> vẽ chim và ống
 * - State -> chữ, Effect -> lệnh cho world + tiếng vỗ cánh
 *
 * Nhạc nền lặp mãi và gắn với scope của Scene nên tự dừng khi Scene bị huỷ.
 * Trên web, trình duyệt chỉ cho phát âm thanh sau lần chạm/phím đầu tiên.
 */
  override suspend fun SContainer.sceneMain() {
    val frames = mapOf(
      WingFrame.IDLE to resourcesVfs["bird_idle.png"].readBitmapSlice(),
      WingFrame.UP to resourcesVfs["bird_up.png"].readBitmapSlice(),
      WingFrame.DOWN to resourcesVfs["bird_down.png"].readBitmapSlice(),
    )
    val idle: BmpSlice = frames.getValue(WingFrame.IDLE)
    val flapSound: Sound = resourcesVfs["flap.wav"].readSound()
    musicChannel = resourcesVfs["music.mp3"].readMusic()
      .play(this@GameScene.coroutineContext, PlaybackParameters(times = infinitePlaybackTimes, volume = 0.4))

    solidRect(config.width, config.height, Palette.Sky)
    val pipeLayer = container()
    solidRect(config.width, config.height - config.groundTop, Palette.Ground).position(0.0, config.groundTop)
    solidRect(config.width, config.grassHeight, Palette.Grass).position(0.0, config.groundTop)

    lateinit var birdImage: Image
    val bird = container {
      position(config.birdX, config.birdStartY)
      birdImage = image(idle) { position(-idle.width / 2.0, -idle.height / 2.0) }
    }

    val scoreText = centeredLabel("0", TextSize.Score).position(config.width / 2, 20.0)
    val greetingText = centeredLabel("", TextSize.Body).position(config.width / 2, 150.0)
    val messageText = centeredLabel("", TextSize.Body).position(config.width / 2, 200.0)

    val pipeRenderer = PipeRenderer(pipeLayer, config)
    val listener = object : WorldListener {
      override fun onPipePassed() = viewModel.setEvent(GameContract.Event.OnPipePassed)
      override fun onCrashed() = viewModel.setEvent(GameContract.Event.OnCrashed)
      override fun onBirdLanded() = viewModel.setEvent(GameContract.Event.OnBirdLanded)
    }

    runSystems(
      views.input.keys,
      listOf(
        ActionInput { action ->
          if (action == GameAction.FLAP) viewModel.setEvent(GameContract.Event.OnFlapPressed)
        },
        GameSystem { _, dt ->
          world.update(dt.toDouble(DurationUnit.SECONDS), viewModel.state.value.phase, listener)
          bird.y = world.bird.y
          bird.rotation = world.bird.tiltDegrees.degrees
          birdImage.bitmap = frames.getValue(world.wingFrame)
          pipeRenderer.render(world)
        },
      ),
    )

    onDown { viewModel.setEvent(GameContract.Event.OnFlapPressed) }

    collectState(viewModel.state) { state ->
      scoreText.text = state.score.toString()
      greetingText.text = state.greeting
      messageText.text = state.message
      messageText.color = if (state.isNewBest) Palette.Highlight else Palette.TextPrimary
    }
    collectEffects(viewModel.effect) { effect ->
      when (effect) {
        GameContract.Effect.StartRun -> {
          world.start()
          flapSound.play(this@GameScene.coroutineContext)
        }
        GameContract.Effect.Flap -> {
          world.flap()
          flapSound.play(this@GameScene.coroutineContext)
        }
        GameContract.Effect.ResetWorld -> world.reset()
      }
    }
  }

/** Dừng nhạc và huỷ ViewModel. */
  override suspend fun sceneDestroy() {
    musicChannel?.stop()
    viewModel.clear()
  }
}

/** Giữ một cặp [SolidRect] cho mỗi ống của world (ghép theo id). */
private class PipeRenderer(private val layer: Container, private val config: GameConfig) {
  private val views = mutableMapOf<Int, Pair<SolidRect, SolidRect>>()
  private val alive = mutableSetOf<Int>()

/** Đồng bộ view với danh sách ống: tạo view cho ống mới, cập nhật x, xoá view của ống đã mất. */
  fun render(world: FlappyWorld) {
    alive.clear()
    for (pipe in world.pipes) {
      alive += pipe.id
      val (top, bottom) = views.getOrPut(pipe.id) {
        val bottomY = pipe.gapY + config.pipeGap
        layer.solidRect(config.pipeWidth, pipe.gapY, Palette.Pipe) to
          layer.solidRect(config.pipeWidth, config.groundTop - bottomY, Palette.Pipe).position(0.0, bottomY)
      }
      top.x = pipe.x
      bottom.x = pipe.x
    }
    val iterator = views.entries.iterator()
    while (iterator.hasNext()) {
      val (id, pair) = iterator.next()
      if (id !in alive) {
        pair.first.removeFromParent()
        pair.second.removeFromParent()
        iterator.remove()
      }
    }
  }
}
