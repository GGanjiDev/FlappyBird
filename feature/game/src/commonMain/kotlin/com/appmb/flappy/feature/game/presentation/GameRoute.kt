/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.feature.game.presentation

import korlibs.inject.Injector
import korlibs.korge.scene.SceneContainer

/** Đăng ký đích đến (destination) màn hình game. */
fun Injector.gameDestination() {
  mapPrototype { GameScene(get(), get(), get()) }
}

/** Mở màn hình game. */
suspend fun SceneContainer.navigateToGame() {
  changeTo<GameScene>()
}
