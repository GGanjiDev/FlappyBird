/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.model.config

/**
 * Mọi con số cần chỉnh khi cân bằng game. Đơn vị: pixel và giây.
 * Chim đứng yên theo trục x; thế giới trôi về phía nó.
 */
data class GameConfig(
  val width: Double = 800.0,
  val height: Double = 600.0,
  /** y của mép trên mặt đất. */
  val groundTop: Double = 520.0,
  val grassHeight: Double = 12.0,
  val gravity: Double = 1500.0,
  val flapVelocity: Double = -500.0,
  val hitRadius: Double = 19.0,
  val birdX: Double = 200.0,
  val birdStartY: Double = 250.0,
  val pipeWidth: Double = 80.0,
  val pipeGap: Double = 170.0,
  val pipeSpeed: Double = 220.0,
  /** Số giây giữa hai cặp ống. */
  val spawnInterval: Double = 1.5,
  /** Khoảng cách tối thiểu từ khe hở tới trần / mặt đất. */
  val pipeMargin: Double = 70.0,
  /** Ống ở bên trái vị trí này sẽ bị xoá. */
  val pipeDespawnX: Double = -10.0,
  val flapsPerPress: Int = 3,
  /** Số giây cho MỘT nhịp vỗ cánh. */
  val flapDuration: Double = 0.16,
  val hoverAmplitude: Double = 8.0,
  val hoverSpeed: Double = 4.0,
  val minTiltDegrees: Double = -25.0,
  val maxTiltDegrees: Double = 70.0,
  val tiltPerVelocity: Double = 0.05,
) {
/** Thời gian một lần nhấn vỗ cánh = [flapsPerPress] nhịp. */
  val totalFlapTime: Double get() = flapDuration * flapsPerPress
}
