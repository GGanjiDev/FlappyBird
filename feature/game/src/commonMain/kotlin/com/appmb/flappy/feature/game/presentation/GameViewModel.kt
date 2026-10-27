/*
 * Copyright 2026 AppMb
 *
 */
// Package presentation của feature game
package com.appmb.flappy.feature.game.presentation

// Dispatcher mặc định
import com.appmb.flappy.core.base.common.DefaultDispatcherProvider
// Interface dispatcher (test thay được)
import com.appmb.flappy.core.base.common.DispatcherProvider
// Lớp cơ sở MVI
import com.appmb.flappy.core.base.mvi.BaseViewModel
// Use case lấy điểm cao nhất
import com.appmb.flappy.core.domain.usecase.GetBestScoreUseCase
// Use case lấy tên người chơi
import com.appmb.flappy.core.domain.usecase.GetPlayerNameUseCase
// Use case nộp điểm cuối lượt
import com.appmb.flappy.core.domain.usecase.SubmitScoreUseCase
// Các pha của lượt chơi
import com.appmb.flappy.core.model.world.GamePhase

// ViewModel của màn hình game: nhận Event, cập nhật State, phát Effect
class GameViewModel(
  // Chỉ dùng một lần khi khởi tạo nên không cần lưu thành thuộc tính
  getBestScore: GetBestScoreUseCase,
  // Dùng khi kết thúc mỗi lượt
  private val submitScore: SubmitScoreUseCase,
  // Chỉ dùng một lần khi khởi tạo
  getPlayerName: GetPlayerNameUseCase,
  // Dispatcher, mặc định cho game thật
  dispatchers: DispatcherProvider = DefaultDispatcherProvider,
) : BaseViewModel<GameContract.Event, GameContract.State, GameContract.Effect>(
  // State ban đầu mang sẵn điểm cao nhất đã lưu và tên người chơi
  initialState = GameContract.State(best = getBestScore(), playerName = getPlayerName()),
  // Truyền dispatcher cho lớp cha
  dispatchers = dispatchers,
) {

  // Chỉ phân phối mỗi Event tới hàm xử lý riêng
  override fun handleEvents(event: GameContract.Event) {
    when (event) {
      GameContract.Event.OnFlapPressed -> onFlapPressed()
      GameContract.Event.OnPipePassed -> onPipePassed()
      GameContract.Event.OnCrashed -> onCrashed()
      GameContract.Event.OnBirdLanded -> onBirdLanded()
    }
  }

  // Người chơi bấm SPACE
  private fun onFlapPressed() {
    // Lấy State hiện tại
    val current = state.value
    // Hành vi tuỳ theo pha
    when (current.phase) {
      GamePhase.READY -> {
        // Chuyển sang đang chơi
        setState { copy(phase = GamePhase.PLAYING) }
        // Bảo world bắt đầu lượt
        setEffect { GameContract.Effect.StartRun }
      }
      // Đang chơi: bảo world vỗ cánh
      GamePhase.PLAYING -> setEffect { GameContract.Effect.Flap }
      // Đã chết: chỉ chơi lại khi chim đã nằm đất
      GamePhase.DEAD -> if (current.canRetry) {
        // Về State mặc định nhưng giữ điểm cao nhất và tên người chơi
        setState { GameContract.State(best = best, playerName = playerName) }
        // Bảo world dọn ống và đặt chim về chỗ cũ
        setEffect { GameContract.Effect.ResetWorld }
      }
    }
  }

  // World báo chim vừa qua một ống
  private fun onPipePassed() {
    // Chỉ tính điểm khi đang chơi
    if (state.value.phase != GamePhase.PLAYING) return
    // Cộng 1 điểm
    setState { copy(score = score + 1) }
  }

  // World báo chim vừa va chạm
  private fun onCrashed() {
    // Lấy State hiện tại
    val current = state.value
    // Mỗi lượt chỉ chết một lần
    if (current.phase != GamePhase.PLAYING) return
    // Nộp điểm, nhận về kỷ lục mới (nếu có)
    val result = submitScore(current.score)
    // Chuyển sang pha chết, cập nhật kỷ lục, chưa cho chơi lại
    setState { copy(phase = GamePhase.DEAD, best = result.best, isNewBest = result.isNewBest, canRetry = false) }
  }

  // World báo chim vừa nằm xuống đất
  private fun onBirdLanded() {
    // Chỉ có ý nghĩa khi đã chết
    if (state.value.phase != GamePhase.DEAD) return
    // Cho phép bấm SPACE để chơi lại
    setState { copy(canRetry = true) }
  }
}
