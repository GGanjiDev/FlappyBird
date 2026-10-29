/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.domain.fake

import com.appmb.flappy.core.domain.repository.BestScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow

class FakeBestScoreRepository(initial: Int = 0) : BestScoreRepository {
  override val best = MutableStateFlow(initial)
  var writes = 0

  override fun setBest(value: Int) {
    best.value = value
    writes++
  }
}
