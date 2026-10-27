/*
 * Copyright 2026 AppMb
 *
 */
// Package cấu hình của module core/model
package com.appmb.flappy.core.model.config

/**
 * Mọi con số cần chỉnh khi cân bằng game. Đơn vị: pixel và giây.
 * Chim đứng yên theo trục x; thế giới trôi về phía nó.
 */
data class GameConfig(
  // Chiều rộng vùng chơi
  val width: Double = 800.0,
  // Chiều cao vùng chơi
  val height: Double = 600.0,
  /** y của mép trên mặt đất. */
  val groundTop: Double = 520.0,
  // Độ dày dải cỏ
  val grassHeight: Double = 12.0,
  // Gia tốc trọng trường (px/s²)
  val gravity: Double = 1500.0,
  // Vận tốc khi vỗ cánh (âm = bay lên)
  val flapVelocity: Double = -500.0,
  // Bán kính hình tròn va chạm của chim
  val hitRadius: Double = 19.0,
  // Vị trí x cố định của chim
  val birdX: Double = 200.0,
  // Vị trí y ban đầu của chim
  val birdStartY: Double = 250.0,
  // Chiều rộng ống
  val pipeWidth: Double = 80.0,
  // Chiều cao khe hở giữa ống trên và ống dưới
  val pipeGap: Double = 170.0,
  // Tốc độ ống trôi sang trái (px/s)
  val pipeSpeed: Double = 220.0,
  /** Số giây giữa hai cặp ống. */
  val spawnInterval: Double = 1.5,
  /** Khoảng cách tối thiểu từ khe hở tới trần / mặt đất. */
  val pipeMargin: Double = 70.0,
  /** Ống ở bên trái vị trí này sẽ bị xoá. */
  val pipeDespawnX: Double = -10.0,
  // Số nhịp vỗ cánh mỗi lần bấm
  val flapsPerPress: Int = 3,
  /** Số giây cho MỘT nhịp vỗ cánh. */
  val flapDuration: Double = 0.16,
  // Biên độ bập bềnh khi chờ (px)
  val hoverAmplitude: Double = 8.0,
  // Tốc độ bập bềnh khi chờ
  val hoverSpeed: Double = 4.0,
  // Góc ngóc đầu lên tối đa (độ)
  val minTiltDegrees: Double = -25.0,
  // Góc chúi đầu xuống tối đa (độ)
  val maxTiltDegrees: Double = 70.0,
  // Hệ số đổi vận tốc rơi sang góc nghiêng
  val tiltPerVelocity: Double = 0.05,
) {
  // Tổng thời gian hoạt ảnh vỗ cánh cho một lần bấm
  val totalFlapTime: Double get() = flapDuration * flapsPerPress
}
