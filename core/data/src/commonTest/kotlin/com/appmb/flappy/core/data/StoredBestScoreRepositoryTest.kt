/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.data

import com.appmb.flappy.core.data.repository.InMemoryKeyValueStorage
import com.appmb.flappy.core.data.repository.StoredBestScoreRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class StoredBestScoreRepositoryTest {
  @Test
  fun `best score survives a restart`() {
    val storage = InMemoryKeyValueStorage()
    StoredBestScoreRepository(storage).setBest(12)

    assertEquals(12, StoredBestScoreRepository(storage).best.value)
  }

  @Test
  fun `missing or corrupted value counts as zero`() {
    assertEquals(0, StoredBestScoreRepository(InMemoryKeyValueStorage()).best.value)
    val corrupted = InMemoryKeyValueStorage(mapOf(StoredBestScoreRepository.KEY to "abc"))
    assertEquals(0, StoredBestScoreRepository(corrupted).best.value)
    val negative = InMemoryKeyValueStorage(mapOf(StoredBestScoreRepository.KEY to "-3"))
    assertEquals(0, StoredBestScoreRepository(negative).best.value)
  }
}
