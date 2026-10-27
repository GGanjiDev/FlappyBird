/*
 * Copyright 2026 AppMb
 *
 */
// Package của module core-base/ui
package com.appmb.flappy.core.base.ui

// Kiểu căn lề chữ
import korlibs.image.text.TextAlignment
// View chứa các view con
import korlibs.korge.view.Container
// View hiển thị chữ
import korlibs.korge.view.Text
// Hàm DSL tạo Text trong Container
import korlibs.korge.view.text

// Tạo một nhãn chữ với màu và cỡ chữ chuẩn của game
fun Container.label(
  // Nội dung chữ
  value: String,
  // Cỡ chữ, mặc định là Body
  size: Int = TextSize.Body,
  // Căn lề, mặc định trên-trái
  alignment: TextAlignment = TextAlignment.TOP_LEFT,
  // Khối cấu hình thêm cho Text
  block: Text.() -> Unit = {},
  // Tạo Text với màu chữ chuẩn rồi thêm vào Container
): Text = text(value, textSize = size, color = Palette.TextPrimary, alignment = alignment, block = block)

/** Nhãn có x là tâm theo chiều ngang (tự căn giữa lại khi đổi chữ). */
fun Container.centeredLabel(value: String, size: Int = TextSize.Body, block: Text.() -> Unit = {}): Text =
  // Dùng căn lề TOP_CENTER để x là điểm giữa của chữ
  label(value, size, TextAlignment.TOP_CENTER, block)
