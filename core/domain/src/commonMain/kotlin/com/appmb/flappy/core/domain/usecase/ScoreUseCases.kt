/*
 * Copyright 2026 AppMb
 *
 */
// Package use case của module core/domain
package com.appmb.flappy.core.domain.usecase

// Interface repository điểm cao nhất
import com.appmb.flappy.core.domain.repository.BestScoreRepository
// Luật so sánh điểm
import com.appmb.flappy.core.domain.rules.ScoreRules

// Use case: lấy điểm cao nhất hiện tại
class GetBestScoreUseCase(private val repository: BestScoreRepository) {
  // Cho phép gọi như hàm: getBestScore()
  operator fun invoke(): Int = repository.best.value
}

// Kết quả của một lượt chơi: điểm, điểm cao nhất, có phải kỷ lục mới không
data class RunResult(val score: Int, val best: Int, val isNewBest: Boolean)

/** Kết thúc một lượt: lưu điểm làm kỷ lục mới nếu vượt kỷ lục cũ. */
class SubmitScoreUseCase(private val repository: BestScoreRepository) {
  // Cho phép gọi như hàm: submitScore(score)
  operator fun invoke(score: Int): RunResult {
    // Kỷ lục trước lượt này
    val previous = repository.best.value
    // Có phá kỷ lục không
    val isNewBest = ScoreRules.isNewBest(previous, score)
    // Chỉ ghi khi có kỷ lục mới (tránh ghi đĩa thừa)
    if (isNewBest) repository.setBest(score)
    // Trả kết quả cho ViewModel hiển thị
    return RunResult(score = score, best = ScoreRules.best(previous, score), isNewBest = isNewBest)
  }
}
