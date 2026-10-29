/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.model.world

/** Pha của một lượt chơi; nằm trong MVI State và là nguồn sự thật duy nhất. */
enum class GamePhase {
  /** Chim bập bềnh, chờ lần vỗ cánh đầu tiên. */
  READY,
  PLAYING,
  /** Chim rơi xuống đất, ống dừng lại. */
  DEAD,
}
