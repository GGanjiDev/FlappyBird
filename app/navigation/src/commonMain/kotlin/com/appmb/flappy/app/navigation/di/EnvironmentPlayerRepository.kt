/*
 * Copyright 2026 AppMb
 *
 */
// Package DI của module app/navigation
package com.appmb.flappy.app.navigation.di

// Interface nguồn thông tin người chơi (từ domain)
import com.appmb.flappy.core.domain.repository.PlayerRepository
// Biến môi trường đa nền tảng của KorGE
import korlibs.io.lang.Environment

/**
 * Đọc tên người chơi từ "biến môi trường" của KorGE:
 * - Web / WebView: query string của trang, vd `https://localhost/?flappy_username=ganji`
 *   (app chủ như KMP-SevenBalls truyền vào qua base URL khi nạp game).
 * - Desktop (JVM): biến môi trường hệ thống, vd `FLAPPY_USERNAME=ganji ./gradlew runJvm`.
 */
class EnvironmentPlayerRepository : PlayerRepository {
  // Environment không phân biệt hoa thường khi đọc key
  override fun playerName(): String? = Environment[KEY]

  companion object {
    // Tên key (query string dùng chữ thường cũng được)
    const val KEY = "FLAPPY_USERNAME"
  }
}
