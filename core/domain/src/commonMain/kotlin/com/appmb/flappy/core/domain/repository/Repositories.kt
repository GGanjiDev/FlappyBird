/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.domain.repository

import kotlinx.coroutines.flow.StateFlow

/** Điểm cao nhất qua mọi lượt chơi (được lưu lại). */
/** Điểm cao nhất, lưu bền giữa các lần chơi. */
interface BestScoreRepository {
  val best: StateFlow<Int>
  fun setBest(value: Int)
}

/** Kho lưu key/value của nền tảng (`views.storage` của KorGE trong app, Map trong test). */
/** Kho key/value tối giản; bản thật do app nối với storage của KorGE. */
interface KeyValueStorage {
  fun read(key: String): String?
  fun write(key: String, value: String)
}

/** Thông tin người chơi do app chủ truyền vào (vd SevenBalls truyền username qua URL của WebView). */
/** Nguồn tên người chơi (null nếu app chủ không truyền). */
interface PlayerRepository {
  fun playerName(): String?
}
