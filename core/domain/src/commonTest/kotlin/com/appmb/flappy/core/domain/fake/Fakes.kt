/*
 * Copyright 2026 AppMb
 *
 */
// Package chứa repository giả dùng trong test
package com.appmb.flappy.core.domain.fake

// Interface cần giả lập
import com.appmb.flappy.core.domain.repository.BestScoreRepository
// StateFlow có thể ghi
import kotlinx.coroutines.flow.MutableStateFlow

// Repository giả: giữ điểm trong bộ nhớ và đếm số lần ghi
class FakeBestScoreRepository(initial: Int = 0) : BestScoreRepository {
  // Điểm cao nhất, khởi tạo từ tham số
  override val best = MutableStateFlow(initial)
  // Số lần setBest đã được gọi
  var writes = 0

  override fun setBest(value: Int) {
    // Cập nhật điểm
    best.value = value
    // Tăng bộ đếm số lần ghi
    writes++
  }
}
