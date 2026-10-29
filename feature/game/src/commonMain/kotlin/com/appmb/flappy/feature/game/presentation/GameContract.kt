/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.feature.game.presentation

import com.appmb.flappy.core.base.mvi.ViewEvent
import com.appmb.flappy.core.base.mvi.ViewSideEffect
import com.appmb.flappy.core.base.mvi.ViewState
import com.appmb.flappy.core.model.world.GamePhase

/** Hợp đồng MVI của màn hình game: Event vào, State ra, Effect một lần. */
class GameContract {
/** Input của người chơi và các sự kiện đáng kể do world báo (qua ống, va chạm, chạm đất). */
  sealed class Event : ViewEvent {
    /** SPACE hoặc chạm màn hình: bắt đầu, vỗ cánh hoặc chơi lại tuỳ theo pha. */
    data object OnFlapPressed : Event()

    data object OnPipePassed : Event()
    data object OnCrashed : Event()
    data object OnBirdLanded : Event()
  }

  /** Lệnh cho world chạy thời gian thực (world giữ vị trí chim và các ống). */
  sealed class Effect : ViewSideEffect {
    data object StartRun : Effect()
    data object Flap : Effect()
    data object ResetWorld : Effect()
  }

/** Trạng thái màn hình; [greeting] và [message] được tính từ các trường còn lại. */
  data class State(
    val phase: GamePhase = GamePhase.READY,
    val score: Int = 0,
    val best: Int = 0,
    val isNewBest: Boolean = false,
    /** Sau khi chết, chỉ được chơi lại khi chim đã nằm trên mặt đất. */
    val canRetry: Boolean = false,
    /** Tên người chơi do app chủ truyền vào, null nếu không có. */
    val playerName: String? = null,
  ) : ViewState {
    val greeting: String
      get() = if (phase == GamePhase.READY && playerName != null) "Welcome $playerName" else ""

    val message: String
      get() = when (phase) {
        GamePhase.READY -> "Tap or press SPACE"
        GamePhase.PLAYING -> ""
        GamePhase.DEAD -> (if (isNewBest) "New best: $best" else "Game over - Best: $best") + " - tap to retry"
      }
  }
}
