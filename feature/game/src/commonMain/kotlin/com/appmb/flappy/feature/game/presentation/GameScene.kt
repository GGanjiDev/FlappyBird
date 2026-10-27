/*
 * Copyright 2026 AppMb
 *
 */
// Package presentation của feature game
package com.appmb.flappy.feature.game.presentation

// Bảng màu
import com.appmb.flappy.core.base.ui.Palette
// Cỡ chữ
import com.appmb.flappy.core.base.ui.TextSize
// Tạo nhãn căn giữa
import com.appmb.flappy.core.base.ui.centeredLabel
// Collect Effect từ ViewModel
import com.appmb.flappy.core.base.ui.collectEffects
// Collect State từ ViewModel
import com.appmb.flappy.core.base.ui.collectState
// System đọc phím -> hành động
import com.appmb.flappy.core.engine.ActionInput
// Các hành động của người chơi
import com.appmb.flappy.core.engine.GameAction
// Interface system chạy mỗi frame
import com.appmb.flappy.core.engine.GameSystem
// Hàm chạy danh sách system mỗi frame
import com.appmb.flappy.core.engine.runSystems
// Hằng số cân bằng game
import com.appmb.flappy.core.model.config.GameConfig
// Các hình cánh
import com.appmb.flappy.core.model.world.WingFrame
// World thời gian thực
import com.appmb.flappy.feature.game.world.FlappyWorld
// Interface nhận sự kiện từ world
import com.appmb.flappy.feature.game.world.WorldListener
// Kiểu ảnh (một vùng của bitmap)
import korlibs.image.bitmap.BmpSlice
// Hàm đọc file ảnh thành BmpSlice
import korlibs.image.format.readBitmapSlice
// Hệ thống file trỏ tới thư mục resources/
import korlibs.io.file.std.resourcesVfs
// Sự kiện nhấn chuột / chạm màn hình
import korlibs.korge.input.onDown
// Lớp Scene của KorGE
import korlibs.korge.scene.Scene
// View chứa các view con
import korlibs.korge.view.Container
// View hiển thị ảnh
import korlibs.korge.view.Image
// Container gốc của Scene
import korlibs.korge.view.SContainer
// View hình chữ nhật tô màu
import korlibs.korge.view.SolidRect
// DSL tạo container con
import korlibs.korge.view.container
// DSL tạo ảnh
import korlibs.korge.view.image
// Hàm đặt vị trí view
import korlibs.korge.view.position
// DSL tạo hình chữ nhật
import korlibs.korge.view.solidRect
// Đổi số thành góc theo độ
import korlibs.math.geom.degrees
// Đơn vị thời gian (để đổi Duration sang giây)
import kotlin.time.DurationUnit

/**
 * Màn hình duy nhất. Phần thời gian thực (chim, ống) nằm trong [FlappyWorld] và được vẽ mỗi frame;
 * điểm / pha / thông báo đến từ MVI State.
 */
class GameScene(
  // ViewModel MVI của màn hình
  private val viewModel: GameViewModel,
  // World thời gian thực
  private val world: FlappyWorld,
  // Hằng số cân bằng game (kích thước, vị trí)
  private val config: GameConfig,
) : Scene() {

  // Dựng giao diện khi Scene được mở
  override suspend fun SContainer.sceneMain() {
    // Nạp 3 hình cánh từ thư mục resources
    val frames = mapOf(
      WingFrame.IDLE to resourcesVfs["bird_idle.png"].readBitmapSlice(),
      WingFrame.UP to resourcesVfs["bird_up.png"].readBitmapSlice(),
      WingFrame.DOWN to resourcesVfs["bird_down.png"].readBitmapSlice(),
    )
    // Hình cánh nghỉ (dùng làm hình mặc định và tính kích thước)
    val idle: BmpSlice = frames.getValue(WingFrame.IDLE)

    // Thứ tự khai báo = thứ tự vẽ (khai báo sau đè lên trước).
    // Bầu trời phủ toàn màn hình
    solidRect(config.width, config.height, Palette.Sky)
    // Lớp chứa ống, vẽ sau trời nhưng trước đất
    val pipeLayer = container()
    // Mặt đất
    solidRect(config.width, config.height - config.groundTop, Palette.Ground).position(0.0, config.groundTop)
    // Dải cỏ trên mặt đất
    solidRect(config.width, config.grassHeight, Palette.Grass).position(0.0, config.groundTop)

    // Ảnh chim, gán trong khối bên dưới
    lateinit var birdImage: Image
    // Container của chim: gốc toạ độ = tâm chim (để xoay quanh tâm)
    val bird = container {
      // Đặt ở vị trí xuất phát
      position(config.birdX, config.birdStartY)
      // Ảnh lệch nửa kích thước để tâm ảnh trùng gốc container
      birdImage = image(idle) { position(-idle.width / 2.0, -idle.height / 2.0) }
    }

    // Chữ điểm số ở giữa phía trên
    val scoreText = centeredLabel("0", TextSize.Score).position(config.width / 2, 20.0)
    // Dòng chào người chơi, nằm ngay trên chữ thông báo
    val greetingText = centeredLabel("", TextSize.Body).position(config.width / 2, 150.0)
    // Chữ thông báo ở giữa màn hình
    val messageText = centeredLabel("", TextSize.Body).position(config.width / 2, 200.0)

    // Bộ vẽ ống
    val pipeRenderer = PipeRenderer(pipeLayer, config)
    // Chuyển sự kiện của world thành Event gửi vào ViewModel
    val listener = object : WorldListener {
      override fun onPipePassed() = viewModel.setEvent(GameContract.Event.OnPipePassed)
      override fun onCrashed() = viewModel.setEvent(GameContract.Event.OnCrashed)
      override fun onBirdLanded() = viewModel.setEvent(GameContract.Event.OnBirdLanded)
    }

    // Chạy các system mỗi frame (dùng views.input.keys, không phải DSL keys)
    runSystems(
      views.input.keys,
      listOf(
        // System 1: phím SPACE -> Event OnFlapPressed
        ActionInput { action ->
          if (action == GameAction.FLAP) viewModel.setEvent(GameContract.Event.OnFlapPressed)
        },
        // System 2: chạy world rồi vẽ kết quả
        GameSystem { _, dt ->
          // Cho world chạy dt giây với pha hiện tại trong State
          world.update(dt.toDouble(DurationUnit.SECONDS), viewModel.state.value.phase, listener)
          // Cập nhật vị trí y của chim
          bird.y = world.bird.y
          // Cập nhật góc nghiêng của chim
          bird.rotation = world.bird.tiltDegrees.degrees
          // Cập nhật hình cánh
          birdImage.bitmap = frames.getValue(world.wingFrame)
          // Vẽ lại các ống
          pipeRenderer.render(world)
        },
      ),
    )

    // Chạm màn hình / click chuột cũng là vỗ cánh (cho điện thoại và WebView, nơi không có phím SPACE)
    onDown { viewModel.setEvent(GameContract.Event.OnFlapPressed) }

    // Mỗi khi State đổi: vẽ lại phần chữ
    collectState(viewModel.state) { state ->
      // Hiện điểm
      scoreText.text = state.score.toString()
      // Hiện lời chào (rỗng thì không thấy gì)
      greetingText.text = state.greeting
      // Hiện thông báo
      messageText.text = state.message
      // Kỷ lục mới thì tô vàng, ngược lại màu trắng
      messageText.color = if (state.isNewBest) Palette.Highlight else Palette.TextPrimary
    }
    // Mỗi Effect: ra lệnh cho world
    collectEffects(viewModel.effect) { effect ->
      when (effect) {
        // Bắt đầu lượt
        GameContract.Effect.StartRun -> world.start()
        // Vỗ cánh
        GameContract.Effect.Flap -> world.flap()
        // Đặt lại world
        GameContract.Effect.ResetWorld -> world.reset()
      }
    }
  }

  // Khi Scene bị huỷ
  override suspend fun sceneDestroy() {
    // Huỷ coroutine và đóng kênh effect của ViewModel
    viewModel.clear()
  }
}

/** Giữ một cặp [SolidRect] cho mỗi ống của world (ghép theo id). */
private class PipeRenderer(private val layer: Container, private val config: GameConfig) {
  // id ống -> (hình ống trên, hình ống dưới)
  private val views = mutableMapOf<Int, Pair<SolidRect, SolidRect>>()
  // Tập id ống còn tồn tại trong frame này (tái sử dụng để không cấp phát mỗi frame)
  private val alive = mutableSetOf<Int>()

  // Đồng bộ các view với danh sách ống của world
  fun render(world: FlappyWorld) {
    // Xoá tập id của frame trước
    alive.clear()
    // Duyệt từng ống trong world
    for (pipe in world.pipes) {
      // Ghi nhận ống còn sống
      alive += pipe.id
      // Lấy view có sẵn, chưa có thì tạo mới
      val (top, bottom) = views.getOrPut(pipe.id) {
        // y của đỉnh ống dưới
        val bottomY = pipe.gapY + config.pipeGap
        // Ống trên: từ y=0 cao bằng gapY
        layer.solidRect(config.pipeWidth, pipe.gapY, Palette.Pipe) to
          // Ống dưới: từ bottomY tới mặt đất
          layer.solidRect(config.pipeWidth, config.groundTop - bottomY, Palette.Pipe).position(0.0, bottomY)
      }
      // Cập nhật x của ống trên
      top.x = pipe.x
      // Cập nhật x của ống dưới
      bottom.x = pipe.x
    }
    // Duyệt các view đang có để xoá view của ống đã biến mất
    val iterator = views.entries.iterator()
    while (iterator.hasNext()) {
      // Lấy (id, cặp view)
      val (id, pair) = iterator.next()
      // Ống không còn trong world?
      if (id !in alive) {
        // Gỡ ống trên khỏi màn hình
        pair.first.removeFromParent()
        // Gỡ ống dưới khỏi màn hình
        pair.second.removeFromParent()
        // Xoá khỏi map
        iterator.remove()
      }
    }
  }
}
