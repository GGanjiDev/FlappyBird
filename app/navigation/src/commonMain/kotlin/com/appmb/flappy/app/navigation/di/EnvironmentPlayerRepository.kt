/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.app.navigation.di

import com.appmb.flappy.core.domain.repository.PlayerRepository
import korlibs.io.lang.Environment

/**
 * Đọc tên người chơi từ "biến môi trường" của KorGE:
 * - Web / WebView: query string của trang, vd `https://localhost/?flappy_username=ganji`
 *   (app chủ như KMP-SevenBalls truyền vào qua base URL khi nạp game).
 * - Desktop (JVM): biến môi trường hệ thống, vd `FLAPPY_USERNAME=ganji ./gradlew runJvm`.
 */
class EnvironmentPlayerRepository : PlayerRepository {
  override fun playerName(): String? = Environment[KEY]

  companion object {
    const val KEY = "FLAPPY_USERNAME"
  }
}
