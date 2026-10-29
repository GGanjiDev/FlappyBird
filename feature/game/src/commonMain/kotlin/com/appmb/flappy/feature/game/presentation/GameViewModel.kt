/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.feature.game.presentation

import com.appmb.flappy.core.base.common.DefaultDispatcherProvider
import com.appmb.flappy.core.base.common.DispatcherProvider
import com.appmb.flappy.core.base.mvi.BaseViewModel
import com.appmb.flappy.core.domain.usecase.GetBestScoreUseCase
import com.appmb.flappy.core.domain.usecase.GetPlayerNameUseCase
import com.appmb.flappy.core.domain.usecase.SubmitScoreUseCase
import com.appmb.flappy.core.model.world.GamePhase

/** Giữ pha chơi, điểm và best. Không biết gì về vị trí chim hay ống (đó là việc của world). */
class GameViewModel(
  getBestScore: GetBestScoreUseCase,
  private val submitScore: SubmitScoreUseCase,
  getPlayerName: GetPlayerNameUseCase,
  dispatchers: DispatcherProvider = DefaultDispatcherProvider,
) : BaseViewModel<GameContract.Event, GameContract.State, GameContract.Effect>(
  initialState = GameContract.State(best = getBestScore(), playerName = getPlayerName()),
  dispatchers = dispatchers,
) {
  override fun handleEvents(event: GameContract.Event) {
    when (event) {
      GameContract.Event.OnFlapPressed -> onFlapPressed()
      GameContract.Event.OnPipePassed -> onPipePassed()
      GameContract.Event.OnCrashed -> onCrashed()
      GameContract.Event.OnBirdLanded -> onBirdLanded()
    }
  }

/** READY: bắt đầu lượt. PLAYING: vỗ cánh. DEAD: chơi lại khi chim đã nằm trên đất. */
  private fun onFlapPressed() {
    val current = state.value
    when (current.phase) {
      GamePhase.READY -> {
        setState { copy(phase = GamePhase.PLAYING) }
        setEffect { GameContract.Effect.StartRun }
      }
      GamePhase.PLAYING -> setEffect { GameContract.Effect.Flap }
      GamePhase.DEAD -> if (current.canRetry) {
        setState { GameContract.State(best = best, playerName = playerName) }
        setEffect { GameContract.Effect.ResetWorld }
      }
    }
  }

/** Cộng 1 điểm khi đang chơi. */
  private fun onPipePassed() {
    if (state.value.phase != GamePhase.PLAYING) return
    setState { copy(score = score + 1) }
  }

/** Chuyển sang DEAD và nộp điểm; chưa cho chơi lại ngay. */
  private fun onCrashed() {
    val current = state.value
    if (current.phase != GamePhase.PLAYING) return
    val result = submitScore(current.score)
    setState { copy(phase = GamePhase.DEAD, best = result.best, isNewBest = result.isNewBest, canRetry = false) }
  }

/** Chim đã nằm trên đất: cho phép chơi lại. */
  private fun onBirdLanded() {
    if (state.value.phase != GamePhase.DEAD) return
    setState { copy(canRetry = true) }
  }
}
