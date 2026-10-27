/*
 * Copyright 2026 AppMb
 *
 */
// Package DI của feature game
package com.appmb.flappy.feature.game.di

// ViewModel của màn hình game
import com.appmb.flappy.feature.game.presentation.GameViewModel
// World thời gian thực
import com.appmb.flappy.feature.game.world.FlappyWorld
// Bộ DI của KorGE
import korlibs.inject.Injector

// Đăng ký các đối tượng của feature game vào DI
fun Injector.gameModule() {
  // Mỗi Scene có ViewModel riêng (prototype = tạo mới mỗi lần get)
  mapPrototype { GameViewModel(get(), get(), get()) }
  // Mỗi Scene có World riêng
  mapPrototype { FlappyWorld(get()) }
}
