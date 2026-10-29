/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.model.world

/** Trạng thái vật lý của chim. `y` là tâm của chim. */
/** Trạng thái vật lý của chim (x cố định, chỉ y thay đổi). `velocityY` dương là rơi xuống. */
data class Bird(
  val y: Double,
  val velocityY: Double = 0.0,
  val tiltDegrees: Double = 0.0,
  val onGround: Boolean = false,
)

/** Hình cánh đang hiển thị. */
/** Hình cánh đang hiển thị. */
enum class WingFrame {
  IDLE,
  UP,
  DOWN,
}
