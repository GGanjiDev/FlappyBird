/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.app.navigation

import com.appmb.flappy.app.navigation.di.coreModule
import com.appmb.flappy.feature.game.di.gameModule
import com.appmb.flappy.feature.game.presentation.gameDestination
import com.appmb.flappy.feature.game.presentation.navigateToGame
import korlibs.inject.Injector
import korlibs.korge.scene.SceneContainer
import korlibs.korge.view.Views

/**
 * Nơi duy nhất biết mọi feature.
 * 1. đăng ký các module DI, 2. đăng ký destination và nối các feature với nhau.
 */
fun Injector.registerAppGraph(views: Views) {
  coreModule(views)
  gameModule()

  gameDestination()
}

/** Màn hình đầu tiên của game. */
/** Mở màn hình đầu tiên khi app khởi động. */
suspend fun SceneContainer.startGame() {
  navigateToGame()
}
