/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.base.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Cung cấp dispatcher qua DI để có thể thay thế. Test truyền vào một TestDispatcher.
 * `Dispatchers.IO` không có trên mọi target của KorGE nên chỉ cung cấp `default`.
 */
interface DispatcherProvider {
  val default: CoroutineDispatcher
}

object DefaultDispatcherProvider : DispatcherProvider {
  override val default: CoroutineDispatcher = Dispatchers.Default
}
