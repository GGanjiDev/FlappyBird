/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.model.world

/**
 * Một cặp ống có chung [x]. Khe hở bắt đầu tại [gapY] (đáy của ống trên)
 * và cao `GameConfig.pipeGap`. [id] giúp phần vẽ ghép view với đúng ống.
 */
data class Pipe(
  val id: Int,
  val x: Double,
  val gapY: Double,
  /** Đã được tính điểm chưa (mỗi ống chỉ tính một lần). */
  val scored: Boolean = false,
)
