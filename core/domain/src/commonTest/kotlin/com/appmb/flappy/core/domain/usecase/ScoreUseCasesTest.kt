/*
 * Copyright 2026 AppMb
 *
 */
// Package test trùng package của use case
package com.appmb.flappy.core.domain.usecase

// Repository giả
import com.appmb.flappy.core.domain.fake.FakeBestScoreRepository
// Annotation đánh dấu hàm test
import kotlin.test.Test
// Hàm so sánh bằng nhau
import kotlin.test.assertEquals

class ScoreUseCasesTest {
  @Test
  fun `a better score becomes the new best`() {
    // Kỷ lục cũ là 5
    val repository = FakeBestScoreRepository(initial = 5)

    // Nộp điểm 8
    val result = SubmitScoreUseCase(repository)(8)

    // Kết quả báo kỷ lục mới là 8
    assertEquals(RunResult(score = 8, best = 8, isNewBest = true), result)
    // Repository đã lưu 8
    assertEquals(8, GetBestScoreUseCase(repository)())
  }

  @Test
  fun `a lower or equal score keeps the best and writes nothing`() {
    // Kỷ lục cũ là 5
    val repository = FakeBestScoreRepository(initial = 5)

    // Điểm thấp hơn: giữ kỷ lục 5
    assertEquals(RunResult(score = 3, best = 5, isNewBest = false), SubmitScoreUseCase(repository)(3))
    // Điểm bằng: cũng không phải kỷ lục mới
    assertEquals(RunResult(score = 5, best = 5, isNewBest = false), SubmitScoreUseCase(repository)(5))
    // Không có lần ghi nào
    assertEquals(0, repository.writes)
  }
}
