/*
 * Copyright 2026 AppMb
 *
 */
// Package của module core/engine
package com.appmb.flappy.core.engine

// Danh sách mã phím của KorGE
import korlibs.event.Key
// Trạng thái bàn phím của KorGE
import korlibs.korge.input.InputKeys
// Kiểu khoảng thời gian
import kotlin.time.Duration

/** Các ý định rời rạc của người chơi. */
enum class GameAction {
  /** Bắt đầu / vỗ cánh / chơi lại tuỳ theo pha. */
  FLAP,
}

/** Bảng ánh xạ phím -> hành động. Chỗ duy nhất để đổi phím điều khiển. */
object InputBindings {
  // Bảng mặc định
  val default: List<Pair<Key, GameAction>> = listOf(
    // SPACE = vỗ cánh
    Key.SPACE to GameAction.FLAP,
  )
}

/** Gọi [onAction] cho mỗi phím đã gán được bấm trong frame này. */
class ActionInput(
  // Bảng phím -> hành động
  private val bindings: List<Pair<Key, GameAction>> = InputBindings.default,
  // Hàm được gọi khi có hành động
  private val onAction: (GameAction) -> Unit,
) : GameSystem {
  // Cho phép tạm tắt nhận input
  var isEnabled: Boolean = true

  override fun update(keys: InputKeys, dt: Duration) {
    // Đang tắt thì bỏ qua
    if (!isEnabled) return
    // Duyệt từng phím đã gán
    for (i in bindings.indices) {
      // Tách cặp (phím, hành động)
      val (key, action) = bindings[i]
      // Chỉ phát khi phím vừa được bấm ở frame này (không phải đang giữ)
      if (keys.justPressed(key)) onAction(action)
    }
  }
}
