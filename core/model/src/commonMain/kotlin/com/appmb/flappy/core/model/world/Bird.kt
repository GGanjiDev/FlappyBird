/*
 * Copyright 2026 AppMb
 *
 */
// Package thế giới game của module core/model
package com.appmb.flappy.core.model.world

/** Trạng thái vật lý của chim. `y` là tâm của chim. */
data class Bird(
  // Toạ độ y của tâm chim
  val y: Double,
  // Vận tốc theo trục y (dương = rơi xuống)
  val velocityY: Double = 0.0,
  // Góc nghiêng hiện tại (độ)
  val tiltDegrees: Double = 0.0,
  // Chim đã nằm trên mặt đất chưa
  val onGround: Boolean = false,
)

/** Hình cánh đang hiển thị. */
enum class WingFrame {
  // Cánh ở giữa (nghỉ)
  IDLE,
  // Cánh giơ lên
  UP,
  // Cánh hạ xuống
  DOWN,
}
