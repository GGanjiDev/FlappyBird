/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.base.ui

import korlibs.image.text.TextAlignment
import korlibs.korge.view.Container
import korlibs.korge.view.Text
import korlibs.korge.view.text

/** Chữ trắng theo cỡ chữ chung của game. */
fun Container.label(
  value: String,
  size: Int = TextSize.Body,
  alignment: TextAlignment = TextAlignment.TOP_LEFT,
  block: Text.() -> Unit = {},
): Text = text(value, textSize = size, color = Palette.TextPrimary, alignment = alignment, block = block)

/** Nhãn có x là tâm theo chiều ngang (tự căn giữa lại khi đổi chữ). */
/** Chữ căn giữa quanh toạ độ x của nó (dùng cho điểm và thông báo). */
fun Container.centeredLabel(value: String, size: Int = TextSize.Body, block: Text.() -> Unit = {}): Text =
  label(value, size, TextAlignment.TOP_CENTER, block)
