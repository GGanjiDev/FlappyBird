/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.feature.game.di

import com.appmb.flappy.feature.game.presentation.GameViewModel
import com.appmb.flappy.feature.game.world.FlappyWorld
import korlibs.inject.Injector

/** ViewModel và world tạo mới cho mỗi lần mở màn hình (prototype). */
fun Injector.gameModule() {
  mapPrototype { GameViewModel(get(), get(), get()) }
  mapPrototype { FlappyWorld(get()) }
}
