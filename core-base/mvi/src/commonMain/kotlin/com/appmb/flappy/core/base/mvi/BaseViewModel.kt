/*
 * Copyright 2026 AppMb
 *
 */
// Khai báo package của module core-base/mvi
package com.appmb.flappy.core.base.mvi

// Bản dispatcher mặc định dùng khi không truyền vào
import com.appmb.flappy.core.base.common.DefaultDispatcherProvider
// Interface cung cấp dispatcher (để test thay được)
import com.appmb.flappy.core.base.common.DispatcherProvider
// Phạm vi sống của các coroutine
import kotlinx.coroutines.CoroutineScope
// Job mà lỗi của coroutine con không làm huỷ các coroutine anh em
import kotlinx.coroutines.SupervisorJob
// Hàm huỷ toàn bộ coroutine trong scope
import kotlinx.coroutines.cancel
// Kênh truyền dữ liệu một lần (dùng cho Effect)
import kotlinx.coroutines.channels.Channel
// Luồng dữ liệu bất đồng bộ
import kotlinx.coroutines.flow.Flow
// StateFlow có thể ghi (giữ State hiện tại)
import kotlinx.coroutines.flow.MutableStateFlow
// StateFlow chỉ đọc (lộ ra cho Scene)
import kotlinx.coroutines.flow.StateFlow
// Chuyển MutableStateFlow thành StateFlow chỉ đọc
import kotlinx.coroutines.flow.asStateFlow
// Chuyển Channel thành Flow để Scene collect
import kotlinx.coroutines.flow.receiveAsFlow
// Cập nhật StateFlow một cách nguyên tử (atomic)
import kotlinx.coroutines.flow.update

// Interface đánh dấu: mọi Event (hành động gửi vào ViewModel) phải implement
interface ViewEvent

// Interface đánh dấu: mọi State (trạng thái màn hình) phải implement
interface ViewState

// Interface đánh dấu: mọi Effect (sự kiện một lần gửi ra Scene) phải implement
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
  // State ban đầu của màn hình
  initialState: State,
  // Nguồn dispatcher (mặc định là bản dùng cho game thật)
  dispatchers: DispatcherProvider = DefaultDispatcherProvider,
) {

  // Scope cho coroutine của ViewModel, bị huỷ khi gọi clear()
  protected val viewModelScope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatchers.default)

  // State nội bộ, có thể ghi
  private val _state = MutableStateFlow(initialState)
  // State công khai, chỉ đọc, Scene collect để render
  val state: StateFlow<State> = _state.asStateFlow()

  // Kênh Effect nội bộ, có buffer để không mất effect
  private val _effect = Channel<Effect>(Channel.BUFFERED)
  // Effect công khai dạng Flow, mỗi effect chỉ được nhận một lần
  val effect: Flow<Effect> = _effect.receiveAsFlow()

  // Lớp con cài đặt: xử lý từng Event
  protected abstract fun handleEvents(event: Event)

  // Điểm vào duy nhất để Scene/engine gửi Event
  fun setEvent(event: Event) {
    // Xử lý ngay lập tức (đồng bộ)
    handleEvents(event)
  }

  // Cập nhật State bằng một hàm biến đổi (thường là copy(...))
  protected fun setState(reducer: State.() -> State) {
    // update đảm bảo đọc-sửa-ghi nguyên tử
    _state.update(reducer)
  }

  // Gửi một Effect ra Scene
  protected fun setEffect(builder: () -> Effect) {
    // trySend không treo; buffer đủ lớn nên không mất effect
    _effect.trySend(builder())
  }

  /** Chủ sở hữu (Scene / overlay) phải gọi khi bị huỷ. */
  open fun clear() {
    // Huỷ mọi coroutine đang chạy của ViewModel
    viewModelScope.cancel()
    // Đóng kênh Effect, collector sẽ kết thúc
    _effect.close()
  }
}
