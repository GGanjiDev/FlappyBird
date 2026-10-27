/*
 * Copyright 2026 AppMb
 *
 */
// Package của module core-base/ui
package com.appmb.flappy.core.base.ui

// Phạm vi chạy coroutine (Scene là một CoroutineScope)
import kotlinx.coroutines.CoroutineScope
// Đại diện một coroutine đang chạy
import kotlinx.coroutines.Job
// Luồng dữ liệu bất đồng bộ
import kotlinx.coroutines.flow.Flow
// Luồng giữ giá trị hiện tại
import kotlinx.coroutines.flow.StateFlow
// Hàm khởi chạy coroutine mới
import kotlinx.coroutines.launch

/**
 * State -> View. Gọi từ Scene (CoroutineScope của Scene chạy trên thread KorGE và bị huỷ
 * khi Scene bị huỷ). [render] phải idempotent (gọi nhiều lần cùng State cho cùng kết quả).
 */
fun <S> CoroutineScope.collectState(state: StateFlow<S>, render: (S) -> Unit): Job = launch {
  // StateFlow vốn đã bỏ qua giá trị trùng, nên chỉ cần collect và render mỗi State mới
  state.collect { render(it) }
}

/** Effect -> phản ứng một lần (âm thanh, thông báo, điều hướng, lệnh cho world). */
fun <E> CoroutineScope.collectEffects(effects: Flow<E>, handle: suspend (E) -> Unit): Job = launch {
  // Mỗi effect nhận được thì gọi handle một lần
  effects.collect { handle(it) }
}
