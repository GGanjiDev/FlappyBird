/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.domain.rules

/** Luật tính kỷ lục. */
object ScoreRules {
  fun best(previousBest: Int, score: Int): Int = maxOf(previousBest, score)

  /** Phải lớn hơn hẳn: bằng kỷ lục cũ không tính là kỷ lục mới. */
  fun isNewBest(previousBest: Int, score: Int): Boolean = score > previousBest
}
