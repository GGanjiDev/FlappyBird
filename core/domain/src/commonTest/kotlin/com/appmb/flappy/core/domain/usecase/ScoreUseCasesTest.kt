/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.domain.usecase

import com.appmb.flappy.core.domain.fake.FakeBestScoreRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreUseCasesTest {
  @Test
  fun `a better score becomes the new best`() {
    val repository = FakeBestScoreRepository(initial = 5)

    val result = SubmitScoreUseCase(repository)(8)

    assertEquals(RunResult(score = 8, best = 8, isNewBest = true), result)
    assertEquals(8, GetBestScoreUseCase(repository)())
  }

  @Test
  fun `a lower or equal score keeps the best and writes nothing`() {
    val repository = FakeBestScoreRepository(initial = 5)

    assertEquals(RunResult(score = 3, best = 5, isNewBest = false), SubmitScoreUseCase(repository)(3))
    assertEquals(RunResult(score = 5, best = 5, isNewBest = false), SubmitScoreUseCase(repository)(5))
    assertEquals(0, repository.writes)
  }
}
