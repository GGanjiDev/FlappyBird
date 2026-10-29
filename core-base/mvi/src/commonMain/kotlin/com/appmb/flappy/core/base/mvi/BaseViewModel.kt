/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.base.mvi

import com.appmb.flappy.core.base.common.DefaultDispatcherProvider
import com.appmb.flappy.core.base.common.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/** Đánh dấu input gửi vào ViewModel. */
interface ViewEvent

/** Đánh dấu trạng thái bất biến mà Scene vẽ ra. */
interface ViewState

/** Đánh dấu việc chỉ chạy một lần (âm thanh, điều hướng, lệnh cho world). */
interface ViewSideEffect

/**
 * Lớp cơ sở MVI (Event / State / Effect) cho các Scene KorGE, không dùng androidx.
 *
 * Lựa chọn thiết kế:
 * - Không dùng `ViewModel` của androidx: tự quản lý [viewModelScope], Scene gọi [clear] trong `sceneDestroy()`.
 * - [setEvent] gọi [handleEvents] một cách ĐỒNG BỘ. Giữ thứ tự input của game tất định và test
 *   đơn giản (không mất event khi chưa có ai collect). Việc chạy lâu trong [handleEvents]
 *   phải tự `viewModelScope.launch { }`.
 * - [setEffect] dùng channel có buffer + `trySend`, nên effect gửi trước khi Scene bắt đầu
 *   collect vẫn được giao khi Scene collect.
 */
abstract class BaseViewModel<Event : ViewEvent, State : ViewState, Effect : ViewSideEffect>(
  initialState: State,
  dispatchers: DispatcherProvider = DefaultDispatcherProvider,
) {
  protected val viewModelScope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatchers.default)

  private val _state = MutableStateFlow(initialState)
  val state: StateFlow<State> = _state.asStateFlow()

  private val _effect = Channel<Effect>(Channel.BUFFERED)
  val effect: Flow<Effect> = _effect.receiveAsFlow()

  /** Chỉ dispatch Event sang hàm xử lý tương ứng. */
  protected abstract fun handleEvents(event: Event)

  /** Scene gửi Event vào; xử lý đồng bộ ngay trong lời gọi. */
  fun setEvent(event: Event) {
    handleEvents(event)
  }

  /** Cập nhật State bằng reducer `copy(...)`. */
  protected fun setState(reducer: State.() -> State) {
    _state.update(reducer)
  }

  /** Phát một Effect; được giữ trong buffer nếu Scene chưa collect. */
  protected fun setEffect(builder: () -> Effect) {
    _effect.trySend(builder())
  }

  /** Chủ sở hữu (Scene / overlay) phải gọi khi bị huỷ. */
  open fun clear() {
    viewModelScope.cancel()
    _effect.close()
  }
}
