/*
 * Copyright 2026 AppMb
 *
 */
// Package của module core-base/ui
package com.appmb.flappy.core.base.ui

// Bảng màu có sẵn và hàm parse mã màu hex
import korlibs.image.color.Colors
// Kiểu màu RGBA của KorGE
import korlibs.image.color.RGBA

/** Design token. Mọi màu của game đặt ở đây. */
object Palette {
  // Màu nền cửa sổ (ngoài vùng game)
  val Background: RGBA = Colors["#2b2b2b"]
  // Màu bầu trời
  val Sky: RGBA = Colors["#70c5ce"]
  // Màu đất
  val Ground: RGBA = Colors["#ded895"]
  // Màu dải cỏ trên mặt đất
  val Grass: RGBA = Colors["#73bf2e"]
  // Màu ống
  val Pipe: RGBA = Colors["#3fa34d"]
  // Màu chữ thường
  val TextPrimary: RGBA = Colors.WHITE
  // Màu chữ nổi bật (kỷ lục mới)
  val Highlight: RGBA = Colors["#ffd447"]
}

/** Cỡ chữ cho màn hình ảo 800x600. */
object TextSize {
  // Cỡ chữ cho thông báo
  const val Body = 36
  // Cỡ chữ cho điểm số
  const val Score = 56
}
