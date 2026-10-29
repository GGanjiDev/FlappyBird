/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.data.repository

import com.appmb.flappy.core.domain.repository.BestScoreRepository
import com.appmb.flappy.core.domain.repository.KeyValueStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Điểm cao nhất lưu trong [storage]; giá trị thiếu hoặc không đọc được tính là 0. */
/** Best score lưu bền qua [KeyValueStorage]; giá trị hỏng hoặc âm được coi là 0. */
class StoredBestScoreRepository(private val storage: KeyValueStorage) : BestScoreRepository {
  private val _best = MutableStateFlow(storage.read(KEY)?.toIntOrNull()?.coerceAtLeast(0) ?: 0)
  override val best: StateFlow<Int> = _best.asStateFlow()

  override fun setBest(value: Int) {
    _best.value = value
    storage.write(KEY, value.toString())
  }

  companion object {
    const val KEY = "best_score"
  }
}

/** Kho chỉ sống trong một lần chạy (dùng cho test, preview). */
/** Kho key/value trong bộ nhớ, dùng cho test. */
class InMemoryKeyValueStorage(initial: Map<String, String> = emptyMap()) : KeyValueStorage {
  private val data = initial.toMutableMap()
  override fun read(key: String): String? = data[key]
  override fun write(key: String, value: String) {
    data[key] = value
  }
}
