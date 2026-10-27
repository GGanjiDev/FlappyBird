/*
 * Copyright 2026 AppMb
 *
 */
// Package presentation của feature game
package com.appmb.flappy.feature.game.presentation

// Bộ DI của KorGE
import korlibs.inject.Injector
// Bộ chứa Scene (dùng để chuyển màn hình)
import korlibs.korge.scene.SceneContainer

/** Đăng ký đích đến (destination) màn hình game. */
fun Injector.gameDestination() {
  // Mỗi lần mở tạo một GameScene mới; 3 tham số lấy từ DI (ViewModel, World, Config)
  mapPrototype { GameScene(get(), get(), get()) }
}

// Chuyển sang màn hình game
suspend fun SceneContainer.navigateToGame() {
  // KorGE tạo GameScene qua Injector đã đăng ký ở trên
  changeTo<GameScene>()
}
