/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.feature.game

import com.appmb.flappy.core.base.common.DispatcherProvider
import com.appmb.flappy.core.data.repository.InMemoryKeyValueStorage
import com.appmb.flappy.core.data.repository.StoredBestScoreRepository
import com.appmb.flappy.core.domain.repository.PlayerRepository
import com.appmb.flappy.core.domain.usecase.GetBestScoreUseCase
import com.appmb.flappy.core.domain.usecase.GetPlayerNameUseCase
import com.appmb.flappy.core.domain.usecase.SubmitScoreUseCase
import com.appmb.flappy.core.model.world.GamePhase
import com.appmb.flappy.feature.game.presentation.GameContract.Effect
import com.appmb.flappy.feature.game.presentation.GameContract.Event
import com.appmb.flappy.feature.game.presentation.GameViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
  private val repository = StoredBestScoreRepository(InMemoryKeyValueStorage())

  private fun TestScope.viewModel(playerName: String? = null) = GameViewModel(
    getBestScore = GetBestScoreUseCase(repository),
    submitScore = SubmitScoreUseCase(repository),
    getPlayerName = GetPlayerNameUseCase(object : PlayerRepository {
      override fun playerName(): String? = playerName
    }),
    dispatchers = object : DispatcherProvider {
      override val default: CoroutineDispatcher = UnconfinedTestDispatcher(testScheduler)
    },
  )

  @Test
  fun `starts ready with the stored best`() = runTest {
    repository.setBest(7)
    val vm = viewModel()

    assertEquals(GamePhase.READY, vm.state.value.phase)
    assertEquals(7, vm.state.value.best)
    assertEquals("Tap or press SPACE", vm.state.value.message)
    vm.clear()
  }

  @Test
  fun `first press starts the run, next presses flap`() = runTest {
    val vm = viewModel()

    vm.setEvent(Event.OnFlapPressed)
    vm.setEvent(Event.OnFlapPressed)

    assertEquals(GamePhase.PLAYING, vm.state.value.phase)
    assertEquals(listOf(Effect.StartRun, Effect.Flap), vm.effect.take(2).toList())
    vm.clear()
  }

  @Test
  fun `passing pipes scores only while playing`() = runTest {
    val vm = viewModel()
    vm.setEvent(Event.OnPipePassed)
    assertEquals(0, vm.state.value.score)

    vm.setEvent(Event.OnFlapPressed)
    vm.setEvent(Event.OnPipePassed)
    vm.setEvent(Event.OnPipePassed)

    assertEquals(2, vm.state.value.score)
    vm.clear()
  }

  @Test
  fun `crashing ends the run and records a new best`() = runTest {
    repository.setBest(1)
    val vm = viewModel()
    vm.setEvent(Event.OnFlapPressed)
    repeat(3) { vm.setEvent(Event.OnPipePassed) }

    vm.setEvent(Event.OnCrashed)
    vm.setEvent(Event.OnPipePassed)

    val state = vm.state.value
    assertEquals(GamePhase.DEAD, state.phase)
    assertEquals(3, state.score)
    assertEquals(3, state.best)
    assertTrue(state.isNewBest)
    assertEquals(3, repository.best.value)
    vm.clear()
  }

  @Test
  fun `retry is possible only once the bird has landed`() = runTest {
    val vm = viewModel()
    vm.setEvent(Event.OnFlapPressed)
    vm.setEvent(Event.OnPipePassed)
    vm.setEvent(Event.OnCrashed)
    assertEquals(Effect.StartRun, vm.effect.first())

    vm.setEvent(Event.OnFlapPressed)
    assertEquals(GamePhase.DEAD, vm.state.value.phase)
    assertFalse(vm.state.value.canRetry)

    vm.setEvent(Event.OnBirdLanded)
    vm.setEvent(Event.OnFlapPressed)

    val state = vm.state.value
    assertEquals(GamePhase.READY, state.phase)
    assertEquals(0, state.score)
    assertEquals(1, state.best)
    assertEquals(Effect.ResetWorld, vm.effect.first())
    vm.clear()
  }

  @Test
  fun `game over message shows the best score`() = runTest {
    repository.setBest(10)
    val vm = viewModel()
    vm.setEvent(Event.OnFlapPressed)
    vm.setEvent(Event.OnCrashed)

    assertEquals("Game over - Best: 10 - tap to retry", vm.state.value.message)
    vm.clear()
  }

  @Test
  fun `the player is welcomed by name while waiting`() = runTest {
    val vm = viewModel(playerName = "ganji")

    assertEquals("Welcome ganji", vm.state.value.greeting)
    vm.setEvent(Event.OnFlapPressed)
    assertEquals("", vm.state.value.greeting)
    vm.setEvent(Event.OnCrashed)
    vm.setEvent(Event.OnBirdLanded)
    vm.setEvent(Event.OnFlapPressed)
    assertEquals("Welcome ganji", vm.state.value.greeting)
    vm.clear()
  }

  @Test
  fun `no greeting without a player name`() = runTest {
    val vm = viewModel()

    assertEquals("", vm.state.value.greeting)
    vm.clear()
  }
}
