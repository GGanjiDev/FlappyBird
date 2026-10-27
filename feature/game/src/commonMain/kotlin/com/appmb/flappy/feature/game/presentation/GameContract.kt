/*
 * Copyright 2026 AppMb
 *
 */
// Package presentation của feature game
package com.appmb.flappy.feature.game.presentation

// Interface đánh dấu Event
import com.appmb.flappy.core.base.mvi.ViewEvent
// Interface đánh dấu Effect
import com.appmb.flappy.core.base.mvi.ViewSideEffect
// Interface đánh dấu State
import com.appmb.flappy.core.base.mvi.ViewState
// Các pha của lượt chơi
import com.appmb.flappy.core.model.world.GamePhase

// Hợp đồng MVI của màn hình game: Event vào, State ra, Effect một lần
class GameContract {
  // Những gì gửi vào ViewModel
  sealed class Event : ViewEvent {
    /** SPACE hoặc chạm màn hình: bắt đầu, vỗ cánh hoặc chơi lại tuỳ theo pha. */
    data object OnFlapPressed : Event()

    // Do world gửi, chỉ khi có điều đáng kể xảy ra:
    // Chim vừa qua một ống
    data object OnPipePassed : Event()
    // Chim vừa va vào ống hoặc mặt đất
    data object OnCrashed : Event()
    // Chim vừa nằm xuống đất
    data object OnBirdLanded : Event()
  }

  /** Lệnh cho world chạy thời gian thực (world giữ vị trí chim và các ống). */
  sealed class Effect : ViewSideEffect {
    // Bắt đầu lượt: sinh ống đầu tiên ngay và vỗ cánh
    data object StartRun : Effect()
    // Vỗ cánh
    data object Flap : Effect()
    // Đưa world về trạng thái ban đầu
    data object ResetWorld : Effect()
  }

  // Trạng thái màn hình (bất biến, mọi trường có giá trị mặc định)
  data class State(
    // Pha hiện tại, mặc định là chờ
    val phase: GamePhase = GamePhase.READY,
    // Điểm của lượt hiện tại
    val score: Int = 0,
    // Điểm cao nhất
    val best: Int = 0,
    // Lượt vừa rồi có phá kỷ lục không
    val isNewBest: Boolean = false,
    /** Sau khi chết, chỉ được chơi lại khi chim đã nằm trên mặt đất. */
    val canRetry: Boolean = false,
    /** Tên người chơi do app chủ truyền vào, null nếu không có. */
    val playerName: String? = null,
  ) : ViewState {
    // Dòng chào phía trên thông báo: chỉ hiện ở màn chờ và khi biết tên người chơi
    val greeting: String
      get() = if (phase == GamePhase.READY && playerName != null) "Welcome $playerName" else ""

    // Dòng thông báo hiển thị giữa màn hình, tính từ State
    val message: String
      get() = when (phase) {
        // Đang chờ: hướng dẫn bấm SPACE hoặc chạm màn hình
        GamePhase.READY -> "Tap or press SPACE"
        // Đang chơi: không hiện gì
        GamePhase.PLAYING -> ""
        // Đã chết: báo điểm cao nhất (hoặc kỷ lục mới) và cách chơi lại
        GamePhase.DEAD -> (if (isNewBest) "New best: $best" else "Game over - Best: $best") + " - tap to retry"
      }
  }
}
