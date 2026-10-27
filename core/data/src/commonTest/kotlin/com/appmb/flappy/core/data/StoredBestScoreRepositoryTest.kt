/*
 * Copyright 2026 AppMb
 *
 */
// Package test của module core/data
package com.appmb.flappy.core.data

// Kho trong bộ nhớ
import com.appmb.flappy.core.data.repository.InMemoryKeyValueStorage
// Repository cần test
import com.appmb.flappy.core.data.repository.StoredBestScoreRepository
// Annotation đánh dấu hàm test
import kotlin.test.Test
// Hàm so sánh bằng nhau
import kotlin.test.assertEquals

class StoredBestScoreRepositoryTest {
  @Test
  fun `best score survives a restart`() {
    // Một kho dùng chung cho hai "lần chạy"
    val storage = InMemoryKeyValueStorage()
    // Lần chạy thứ nhất ghi kỷ lục 12
    StoredBestScoreRepository(storage).setBest(12)

    // Lần chạy thứ hai đọc lại được 12
    assertEquals(12, StoredBestScoreRepository(storage).best.value)
  }

  @Test
  fun `missing or corrupted value counts as zero`() {
    // Kho trống: 0
    assertEquals(0, StoredBestScoreRepository(InMemoryKeyValueStorage()).best.value)
    // Dữ liệu hỏng (không phải số)
    val corrupted = InMemoryKeyValueStorage(mapOf(StoredBestScoreRepository.KEY to "abc"))
    // Hỏng: 0
    assertEquals(0, StoredBestScoreRepository(corrupted).best.value)
    // Dữ liệu âm (không hợp lệ)
    val negative = InMemoryKeyValueStorage(mapOf(StoredBestScoreRepository.KEY to "-3"))
    // Âm: 0
    assertEquals(0, StoredBestScoreRepository(negative).best.value)
  }
}
