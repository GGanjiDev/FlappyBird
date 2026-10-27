/*
 * Copyright 2026 AppMb
 *
 */
// Package test trùng package thư viện
package com.appmb.flappy.sdk

// Annotation đánh dấu hàm test
import kotlin.test.Test
// Hàm so sánh bằng nhau
import kotlin.test.assertEquals

class GameUrlTest {
  @Test
  fun `player name is passed in the query string`() {
    // Tên thường giữ nguyên
    assertEquals("https://localhost/?flappy_username=ganji", gameUrl("ganji"))
  }

  @Test
  fun `blank name adds no query string`() {
    // Tên rỗng: không có query
    assertEquals("https://localhost/", gameUrl("  "))
  }

  @Test
  fun `special and unicode characters are percent encoded`() {
    // Khoảng trắng, &, = và chữ có dấu đều được mã hoá
    assertEquals("Nguy%E1%BB%85n%20A%26B%3D", encodeQueryValue("Nguyễn A&B="))
  }
}
