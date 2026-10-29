/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.base.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * State -> View. Gọi từ Scene (CoroutineScope của Scene chạy trên thread KorGE và bị huỷ
 * khi Scene bị huỷ). [render] phải idempotent (gọi nhiều lần cùng State cho cùng kết quả).
 */
fun <S> CoroutineScope.collectState(state: StateFlow<S>, render: (S) -> Unit): Job = launch {
  state.collect { render(it) }
}

/** Effect -> phản ứng một lần (âm thanh, thông báo, điều hướng, lệnh cho world). */
/** Effect -> phản ứng một lần (âm thanh, lệnh cho world, điều hướng). */
fun <E> CoroutineScope.collectEffects(effects: Flow<E>, handle: suspend (E) -> Unit): Job = launch {
  effects.collect { handle(it) }
}
