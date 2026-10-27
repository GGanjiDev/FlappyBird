/*
 * Copyright 2026 AppMb
 *
 */
// Package thế giới game của module core/model
package com.appmb.flappy.core.model.world

/**
 * Một cặp ống có chung [x]. Khe hở bắt đầu tại [gapY] (đáy của ống trên)
 * và cao `GameConfig.pipeGap`. [id] giúp phần vẽ ghép view với đúng ống.
 */
data class Pipe(
  // Mã định danh duy nhất của cặp ống
  val id: Int,
  // Toạ độ x của mép trái ống
  val x: Double,
  // Toạ độ y của đáy ống trên (đỉnh khe hở)
  val gapY: Double,
  /** Đã được tính điểm chưa (mỗi ống chỉ tính một lần). */
  val scored: Boolean = false,
)
