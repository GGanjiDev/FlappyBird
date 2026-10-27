/*
 * Copyright 2026 AppMb
 *
 */
// Package của module app/navigation
package com.appmb.flappy.app.navigation

// Đăng ký DI phần core (config, repository, use case)
import com.appmb.flappy.app.navigation.di.coreModule
// Đăng ký DI của feature game
import com.appmb.flappy.feature.game.di.gameModule
// Đăng ký destination màn hình game
import com.appmb.flappy.feature.game.presentation.gameDestination
// Hàm chuyển sang màn hình game
import com.appmb.flappy.feature.game.presentation.navigateToGame
// Bộ DI của KorGE
import korlibs.inject.Injector
// Bộ chứa Scene
import korlibs.korge.scene.SceneContainer
// Đối tượng Views toàn cục của KorGE (storage, input...)
import korlibs.korge.view.Views

/**
 * Nơi duy nhất biết mọi feature.
 * 1. đăng ký các module DI, 2. đăng ký destination và nối các feature với nhau.
 */
fun Injector.registerAppGraph(views: Views) {
  // DI dùng chung (cần views để lấy storage)
  coreModule(views)
  // DI của feature game
  gameModule()

  // Đăng ký màn hình game
  gameDestination()
}

/** Màn hình đầu tiên của game. */
suspend fun SceneContainer.startGame() {
  // Mở thẳng màn hình game
  navigateToGame()
}
