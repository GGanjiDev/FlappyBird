/*
 * Copyright 2026 AppMb
 *
 */
// Package cài đặt repository của module core/data
package com.appmb.flappy.core.data.repository

// Interface repository điểm cao nhất (từ domain)
import com.appmb.flappy.core.domain.repository.BestScoreRepository
// Interface kho key/value (từ domain)
import com.appmb.flappy.core.domain.repository.KeyValueStorage
// StateFlow có thể ghi
import kotlinx.coroutines.flow.MutableStateFlow
// StateFlow chỉ đọc
import kotlinx.coroutines.flow.StateFlow
// Chuyển sang StateFlow chỉ đọc
import kotlinx.coroutines.flow.asStateFlow

/** Điểm cao nhất lưu trong [storage]; giá trị thiếu hoặc không đọc được tính là 0. */
class StoredBestScoreRepository(private val storage: KeyValueStorage) : BestScoreRepository {
  // Đọc từ kho khi khởi tạo: parse số, số âm thành 0, lỗi/thiếu thành 0
  private val _best = MutableStateFlow(storage.read(KEY)?.toIntOrNull()?.coerceAtLeast(0) ?: 0)
  // Lộ ra bản chỉ đọc
  override val best: StateFlow<Int> = _best.asStateFlow()

  override fun setBest(value: Int) {
    // Cập nhật trong bộ nhớ (người quan sát được báo ngay)
    _best.value = value
    // Ghi xuống kho để lần sau mở game vẫn còn
    storage.write(KEY, value.toString())
  }

  companion object {
    // Key dùng để lưu điểm cao nhất
    const val KEY = "best_score"
  }
}

/** Kho chỉ sống trong một lần chạy (dùng cho test, preview). */
class InMemoryKeyValueStorage(initial: Map<String, String> = emptyMap()) : KeyValueStorage {
  // Bản sao có thể sửa của dữ liệu ban đầu
  private val data = initial.toMutableMap()
  // Đọc theo key
  override fun read(key: String): String? = data[key]
  override fun write(key: String, value: String) {
    // Ghi đè giá trị cho key
    data[key] = value
  }
}
