/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.base.ui

import korlibs.image.color.Colors
import korlibs.image.color.RGBA

/** Design token. Mọi màu của game đặt ở đây. */
/** Bảng màu dùng chung cho mọi màn hình. */
object Palette {
  val Background: RGBA = Colors["#2b2b2b"]
  val Sky: RGBA = Colors["#70c5ce"]
  val Ground: RGBA = Colors["#ded895"]
  val Grass: RGBA = Colors["#73bf2e"]
  val Pipe: RGBA = Colors["#3fa34d"]
  val TextPrimary: RGBA = Colors.WHITE
  val Highlight: RGBA = Colors["#ffd447"]
}

/** Cỡ chữ cho màn hình ảo 800x600. */
/** Cỡ chữ dùng chung. */
object TextSize {
  const val Body = 36
  const val Score = 56
}
