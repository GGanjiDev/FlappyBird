/*
 * Copyright 2026 AppMb
 *
 */
// Package thế giới game của module core/model
package com.appmb.flappy.core.model.world

// Các pha của một lượt chơi
enum class GamePhase {
  /** Chim bập bềnh, chờ lần vỗ cánh đầu tiên. */
  READY,
  // Đang chơi: chim rơi, ống trôi, tính điểm
  PLAYING,
  /** Chim rơi xuống đất, ống dừng lại. */
  DEAD,
}
