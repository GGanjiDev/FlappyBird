/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.domain.usecase

import com.appmb.flappy.core.domain.repository.BestScoreRepository
import com.appmb.flappy.core.domain.rules.ScoreRules

/** Đọc best score hiện tại. */
class GetBestScoreUseCase(private val repository: BestScoreRepository) {
  operator fun invoke(): Int = repository.best.value
}

/** Kết quả một lượt sau khi chết. */
data class RunResult(val score: Int, val best: Int, val isNewBest: Boolean)

/** Kết thúc một lượt: lưu điểm làm kỷ lục mới nếu vượt kỷ lục cũ. */
/** Nộp điểm khi chết: lưu nếu phá kỷ lục và trả về [RunResult]. */
class SubmitScoreUseCase(private val repository: BestScoreRepository) {
  operator fun invoke(score: Int): RunResult {
    val previous = repository.best.value
    val isNewBest = ScoreRules.isNewBest(previous, score)
    if (isNewBest) repository.setBest(score)
    return RunResult(score = score, best = ScoreRules.best(previous, score), isNewBest = isNewBest)
  }
}
