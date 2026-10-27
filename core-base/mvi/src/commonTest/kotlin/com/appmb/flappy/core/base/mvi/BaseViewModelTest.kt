/*
 * Copyright 2026 AppMb
 *
 */
// Package test trùng package của BaseViewModel
package com.appmb.flappy.core.base.mvi

// Interface dispatcher để truyền dispatcher của test
import com.appmb.flappy.core.base.common.DispatcherProvider
// Kiểu dispatcher
import kotlinx.coroutines.CoroutineDispatcher
// Cho phép dùng API thử nghiệm của coroutines-test
import kotlinx.coroutines.ExperimentalCoroutinesApi
// Lấy phần tử đầu tiên của Flow
import kotlinx.coroutines.flow.first
// Dispatcher chạy coroutine ngay lập tức trong test
import kotlinx.coroutines.test.UnconfinedTestDispatcher
// Chạy một test có coroutine
import kotlinx.coroutines.test.runTest
// Annotation đánh dấu hàm test
import kotlin.test.Test
// Hàm so sánh bằng nhau
import kotlin.test.assertEquals

// Contract mẫu (bộ đếm) chỉ dùng trong test
private class CounterContract {
  // Các Event của bộ đếm
  sealed class Event : ViewEvent {
    // Tăng bộ đếm thêm 1
    data object OnIncrement : Event()
    // Báo đã đạt giới hạn
    data object OnReachedLimit : Event()
  }

  // Các Effect của bộ đếm
  sealed class Effect : ViewSideEffect {
    // Effect một lần: đã đạt giới hạn
    data object LimitReached : Effect()
  }

  // State của bộ đếm, mặc định là 0
  data class State(val count: Int = 0) : ViewState
}

// ViewModel mẫu dùng Contract ở trên
private class CounterViewModel(dispatchers: DispatcherProvider) :
  BaseViewModel<CounterContract.Event, CounterContract.State, CounterContract.Effect>(
    // State ban đầu
    initialState = CounterContract.State(),
    // Dispatcher của test
    dispatchers = dispatchers,
  ) {
  // Xử lý từng Event
  override fun handleEvents(event: CounterContract.Event) {
    when (event) {
      // Tăng count lên 1
      CounterContract.Event.OnIncrement -> setState { copy(count = count + 1) }
      // Gửi effect LimitReached
      CounterContract.Event.OnReachedLimit -> setEffect { CounterContract.Effect.LimitReached }
    }
  }
}

// Cho phép dùng API thử nghiệm trong cả lớp test
@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

  // Bọc một dispatcher thành DispatcherProvider
  private fun testDispatchers(dispatcher: CoroutineDispatcher) = object : DispatcherProvider {
    // Trả về dispatcher của test
    override val default: CoroutineDispatcher = dispatcher
  }

  @Test
  fun `events are reduced into state synchronously`() = runTest {
    // Tạo ViewModel với dispatcher chạy ngay
    val viewModel = CounterViewModel(testDispatchers(UnconfinedTestDispatcher(testScheduler)))

    // Gửi 2 lần tăng
    viewModel.setEvent(CounterContract.Event.OnIncrement)
    viewModel.setEvent(CounterContract.Event.OnIncrement)

    // State phải cập nhật ngay, không cần chờ
    assertEquals(2, viewModel.state.value.count)
    // Dọn dẹp ViewModel
    viewModel.clear()
  }

  @Test
  fun `an effect sent before anyone collects is still delivered`() = runTest {
    // Tạo ViewModel
    val viewModel = CounterViewModel(testDispatchers(UnconfinedTestDispatcher(testScheduler)))

    // Gửi effect khi chưa có ai collect
    viewModel.setEvent(CounterContract.Event.OnReachedLimit)

    // Collect sau vẫn nhận được effect
    assertEquals(CounterContract.Effect.LimitReached, viewModel.effect.first())
    // Dọn dẹp ViewModel
    viewModel.clear()
  }
}
