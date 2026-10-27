/*
 * Copyright 2026 AppMb
 *
 */
// Khai báo package của module core-base/common
package com.appmb.flappy.core.base.common

// Kiểu dispatcher của coroutine (quyết định coroutine chạy trên thread nào)
import kotlinx.coroutines.CoroutineDispatcher
// Tập dispatcher có sẵn của kotlinx.coroutines
import kotlinx.coroutines.Dispatchers

/**
 * Cung cấp dispatcher qua DI để có thể thay thế. Test truyền vào một TestDispatcher.
 * `Dispatchers.IO` không có trên mọi target của KorGE nên chỉ cung cấp `default`.
 */
interface DispatcherProvider {
  // Dispatcher mặc định dùng để chạy coroutine của ViewModel
  val default: CoroutineDispatcher
}

// Bản cài đặt dùng khi chạy game thật
object DefaultDispatcherProvider : DispatcherProvider {
  // Dùng Dispatchers.Default (thread pool dùng chung cho tác vụ CPU)
  override val default: CoroutineDispatcher = Dispatchers.Default
}
