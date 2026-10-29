/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.engine

import korlibs.event.Key
import korlibs.korge.input.InputKeys
import kotlin.time.Duration

/** Các ý định rời rạc của người chơi. */
/** Hành động trừu tượng của người chơi, tách khỏi phím cụ thể. */
enum class GameAction {
  /** Bắt đầu / vỗ cánh / chơi lại tuỳ theo pha. */
  FLAP,
}

/** Bảng ánh xạ phím -> hành động. Chỗ duy nhất để đổi phím điều khiển. */
/** Bảng phím -> hành động mặc định. */
object InputBindings {
  val default: List<Pair<Key, GameAction>> = listOf(
    Key.SPACE to GameAction.FLAP,
  )
}

/** Gọi [onAction] cho mỗi phím đã gán được bấm trong frame này. */
/** System đọc phím mỗi frame và gọi [onAction] khi phím vừa được nhấn (không lặp khi giữ). */
class ActionInput(
  private val bindings: List<Pair<Key, GameAction>> = InputBindings.default,
  private val onAction: (GameAction) -> Unit,
) : GameSystem {
  var isEnabled: Boolean = true

  override fun update(keys: InputKeys, dt: Duration) {
    if (!isEnabled) return
    for (i in bindings.indices) {
      val (key, action) = bindings[i]
      if (keys.justPressed(key)) onAction(action)
    }
  }
}
