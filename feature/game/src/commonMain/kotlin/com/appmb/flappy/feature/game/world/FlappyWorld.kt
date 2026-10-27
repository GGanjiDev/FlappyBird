/*
 * Copyright 2026 AppMb
 *
 */
// Package world (phần chạy thời gian thực) của feature game
package com.appmb.flappy.feature.game.world

// Luật vật lý của chim
import com.appmb.flappy.core.domain.rules.BirdPhysics
// Luật về ống
import com.appmb.flappy.core.domain.rules.PipeRules
// Luật hoạt ảnh cánh
import com.appmb.flappy.core.domain.rules.WingRules
// Hằng số cân bằng game
import com.appmb.flappy.core.model.config.GameConfig
// Model chim
import com.appmb.flappy.core.model.world.Bird
// Các pha của lượt chơi
import com.appmb.flappy.core.model.world.GamePhase
// Model ống
import com.appmb.flappy.core.model.world.Pipe
// Các hình cánh
import com.appmb.flappy.core.model.world.WingFrame
// Bộ sinh số ngẫu nhiên
import kotlin.random.Random

/** Những khoảnh khắc đáng kể world báo cho ViewModel (không bao giờ mỗi frame). */
interface WorldListener {
  // Chim vừa qua một ống
  fun onPipePassed()
  // Chim vừa va chạm
  fun onCrashed()
  // Chim vừa nằm xuống đất
  fun onBirdLanded()
}

/**
 * Trạng thái thời gian thực của một lượt: vị trí chim, các ống, bộ đếm thời gian. Kotlin thuần
 * (không KorGE) nên test được; Scene cho nó chạy mỗi frame rồi vẽ [bird] / [pipes] / [wingFrame].
 * Pha lấy từ MVI State (nguồn sự thật duy nhất); world không bao giờ tự đổi pha.
 */
class FlappyWorld(
  // Hằng số cân bằng game
  private val config: GameConfig,
  // Bộ sinh ngẫu nhiên (test truyền vào seed cố định)
  private val random: Random = Random.Default,
) {
  // Trạng thái chim, bên ngoài chỉ đọc được
  var bird: Bird = Bird(y = config.birdStartY)
    private set
  // Danh sách ống, bên ngoài chỉ đọc được
  val pipes: List<Pipe> get() = _pipes
  // Hình cánh hiện tại, bên ngoài chỉ đọc được
  var wingFrame: WingFrame = WingFrame.IDLE
    private set

  // Danh sách ống nội bộ, có thể sửa
  private val _pipes = mutableListOf<Pipe>()
  // id cho ống tiếp theo
  private var nextPipeId = 0
  // Thời gian đã trôi từ lần sinh ống trước
  private var spawnTimer = 0.0
  // Thời gian đã bập bềnh (dùng cho hàm sin)
  private var hoverTime = 0.0
  // Thời gian còn lại của hoạt ảnh vỗ cánh
  private var flapTimer = 0.0

  /** Lần vỗ cánh đầu của một lượt: ống đầu tiên xuất hiện ngay. */
  fun start() {
    // Đặt bộ đếm đầy để frame kế tiếp sinh ống luôn
    spawnTimer = config.spawnInterval
    // Vỗ cánh
    flap()
  }

  // Vỗ cánh
  fun flap() {
    // Đặt vận tốc bay lên
    bird = BirdPhysics.flap(bird, config)
    // Bắt đầu hoạt ảnh vỗ cánh (3 nhịp)
    flapTimer = config.totalFlapTime
  }

  // Đưa world về trạng thái ban đầu
  fun reset() {
    // Xoá mọi ống
    _pipes.clear()
    // Chim về vị trí xuất phát, vận tốc và góc bằng 0
    bird = Bird(y = config.birdStartY)
    // Đặt lại bộ đếm sinh ống
    spawnTimer = 0.0
    // Đặt lại thời gian bập bềnh
    hoverTime = 0.0
    // Dừng vỗ cánh
    stopWings()
  }

  // Gọi mỗi frame: [dt] giây trôi qua, [phase] lấy từ MVI State
  fun update(dt: Double, phase: GamePhase, listener: WorldListener) {
    // Mỗi pha làm việc khác nhau
    when (phase) {
      GamePhase.READY -> {
        // Tăng thời gian bập bềnh
        hoverTime += dt
        // Chim bập bềnh quanh vị trí xuất phát
        bird = bird.copy(y = BirdPhysics.hoverY(hoverTime, config))
      }
      GamePhase.PLAYING -> {
        // Chim rơi; nếu chưa chạm đất thì mới xử lý ống
        if (stepBird(dt, phase, listener)) stepPipes(dt, listener)
      }
      // Đã chết: chim rơi tới đất; ống đứng yên
      GamePhase.DEAD -> if (!bird.onGround) stepBird(dt, phase, listener)
    }
    // Hoạt ảnh cánh chạy ở mọi pha
    animateWings(dt)
  }

  /** @return false nếu chim vừa đâm xuống đất. */
  private fun stepBird(dt: Double, phase: GamePhase, listener: WorldListener): Boolean {
    // Nhớ trạng thái chạm đất trước bước này
    val wasOnGround = bird.onGround
    // Áp dụng vật lý
    bird = BirdPhysics.step(bird, dt, config)
    // Vừa mới chạm đất ở bước này?
    if (bird.onGround && !wasOnGround) {
      // Đang chơi mà chạm đất = chết
      if (phase == GamePhase.PLAYING) crash(listener)
      // Báo chim đã nằm đất (cho phép chơi lại)
      listener.onBirdLanded()
      // Dừng xử lý tiếp trong frame này
      return false
    }
    // Chim vẫn đang bay
    return true
  }

  // Sinh ống, trôi ống, kiểm tra va chạm, tính điểm, xoá ống cũ
  private fun stepPipes(dt: Double, listener: WorldListener) {
    // Cộng thời gian cho bộ đếm sinh ống
    spawnTimer += dt
    // Đủ thời gian thì sinh ống mới
    if (spawnTimer >= config.spawnInterval) {
      // Đặt lại bộ đếm
      spawnTimer = 0.0
      // Thêm ống mới với id tăng dần
      _pipes += PipeRules.spawn(nextPipeId++, random, config)
    }

    // Duyệt bằng listIterator để vừa sửa vừa xoá được
    val iterator = _pipes.listIterator()
    while (iterator.hasNext()) {
      // Trôi ống sang trái
      var pipe = PipeRules.scroll(iterator.next(), dt, config)

      // Chim va vào ống?
      if (PipeRules.hits(pipe, bird.y, config)) {
        // Lưu vị trí mới của ống
        iterator.set(pipe)
        // Báo chết
        crash(listener)
        // Dừng xử lý các ống còn lại (ống đứng yên từ giờ)
        return
      }
      // Ống vừa qua khỏi chim?
      if (PipeRules.shouldScore(pipe, config)) {
        // Đánh dấu đã tính điểm
        pipe = pipe.copy(scored = true)
        // Báo được 1 điểm
        listener.onPipePassed()
      }
      // Ra khỏi màn hình thì xoá, ngược lại lưu trạng thái mới
      if (PipeRules.isOffScreen(pipe, config)) iterator.remove() else iterator.set(pipe)
    }
  }

  // Xử lý chung khi chết
  private fun crash(listener: WorldListener) {
    // Ngừng vỗ cánh
    stopWings()
    // Báo cho ViewModel
    listener.onCrashed()
  }

  // Dừng hoạt ảnh cánh
  private fun stopWings() {
    // Hết thời gian vỗ
    flapTimer = 0.0
    // Cánh về vị trí nghỉ
    wingFrame = WingFrame.IDLE
  }

  // Cập nhật hình cánh theo thời gian
  private fun animateWings(dt: Double) {
    // Không vỗ cánh thì thôi
    if (flapTimer <= 0.0) return
    // Trừ thời gian đã trôi
    flapTimer -= dt
    // Chọn hình cánh mới
    wingFrame = if (flapTimer <= 0.0) {
      // Hết hoạt ảnh: đặt về 0
      flapTimer = 0.0
      // Cánh nghỉ
      WingFrame.IDLE
    } else {
      // Tính hình cánh theo thời gian đã vỗ
      WingRules.frameAt(config.totalFlapTime - flapTimer, config.flapDuration)
    }
  }
}
