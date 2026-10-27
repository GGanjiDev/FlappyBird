/*
 * Copyright 2026 AppMb
 *
 */
// Package luật game của module core/domain
package com.appmb.flappy.core.domain.rules

// Luật về điểm số
object ScoreRules {
  // Điểm cao nhất mới = lớn hơn trong hai giá trị
  fun best(previousBest: Int, score: Int): Int = maxOf(previousBest, score)

  /** Phải lớn hơn hẳn: bằng kỷ lục cũ không tính là kỷ lục mới. */
  fun isNewBest(previousBest: Int, score: Int): Boolean = score > previousBest
}
