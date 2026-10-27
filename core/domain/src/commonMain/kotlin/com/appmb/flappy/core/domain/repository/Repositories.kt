/*
 * Copyright 2026 AppMb
 *
 */
// Package interface repository của module core/domain
package com.appmb.flappy.core.domain.repository

// Luồng giữ giá trị hiện tại, có thể quan sát
import kotlinx.coroutines.flow.StateFlow

/** Điểm cao nhất qua mọi lượt chơi (được lưu lại). */
interface BestScoreRepository {
  // Điểm cao nhất hiện tại, có thể quan sát thay đổi
  val best: StateFlow<Int>
  // Ghi điểm cao nhất mới
  fun setBest(value: Int)
}

/** Kho lưu key/value của nền tảng (`views.storage` của KorGE trong app, Map trong test). */
interface KeyValueStorage {
  // Đọc giá trị theo key, null nếu chưa có
  fun read(key: String): String?
  // Ghi giá trị cho key
  fun write(key: String, value: String)
}

/** Thông tin người chơi do app chủ truyền vào (vd SevenBalls truyền username qua URL của WebView). */
interface PlayerRepository {
  // Tên người chơi, null nếu app chủ không truyền
  fun playerName(): String?
}
