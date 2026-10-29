/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.base.mvi

import com.appmb.flappy.core.base.common.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class CounterContract {
  sealed class Event : ViewEvent {
    data object OnIncrement : Event()
    data object OnReachedLimit : Event()
  }

  sealed class Effect : ViewSideEffect {
    data object LimitReached : Effect()
  }

  data class State(val count: Int = 0) : ViewState
}

private class CounterViewModel(dispatchers: DispatcherProvider) :
  BaseViewModel<CounterContract.Event, CounterContract.State, CounterContract.Effect>(
    initialState = CounterContract.State(),
    dispatchers = dispatchers,
  ) {
  override fun handleEvents(event: CounterContract.Event) {
    when (event) {
      CounterContract.Event.OnIncrement -> setState { copy(count = count + 1) }
      CounterContract.Event.OnReachedLimit -> setEffect { CounterContract.Effect.LimitReached }
    }
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {
  private fun testDispatchers(dispatcher: CoroutineDispatcher) = object : DispatcherProvider {
    override val default: CoroutineDispatcher = dispatcher
  }

  @Test
  fun `events are reduced into state synchronously`() = runTest {
    val viewModel = CounterViewModel(testDispatchers(UnconfinedTestDispatcher(testScheduler)))

    viewModel.setEvent(CounterContract.Event.OnIncrement)
    viewModel.setEvent(CounterContract.Event.OnIncrement)

    assertEquals(2, viewModel.state.value.count)
    viewModel.clear()
  }

  @Test
  fun `an effect sent before anyone collects is still delivered`() = runTest {
    val viewModel = CounterViewModel(testDispatchers(UnconfinedTestDispatcher(testScheduler)))

    viewModel.setEvent(CounterContract.Event.OnReachedLimit)

    assertEquals(CounterContract.Effect.LimitReached, viewModel.effect.first())
    viewModel.clear()
  }
}
