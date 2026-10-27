/*
 * Copyright 2026 AppMb
 *
 */
// Package DI của module app/navigation
package com.appmb.flappy.app.navigation.di

// Repository lưu điểm cao nhất vào kho key/value
import com.appmb.flappy.core.data.repository.StoredBestScoreRepository
// Interface repository điểm cao nhất
import com.appmb.flappy.core.domain.repository.BestScoreRepository
// Interface kho key/value
import com.appmb.flappy.core.domain.repository.KeyValueStorage
// Interface nguồn thông tin người chơi
import com.appmb.flappy.core.domain.repository.PlayerRepository
// Use case lấy điểm cao nhất
import com.appmb.flappy.core.domain.usecase.GetBestScoreUseCase
// Use case lấy tên người chơi
import com.appmb.flappy.core.domain.usecase.GetPlayerNameUseCase
// Use case nộp điểm
import com.appmb.flappy.core.domain.usecase.SubmitScoreUseCase
// Hằng số cân bằng game
import com.appmb.flappy.core.model.config.GameConfig
// Bộ DI của KorGE
import korlibs.inject.Injector
// Extension lấy kho lưu trữ từ Views
import korlibs.korge.service.storage.storage
// Đối tượng Views toàn cục của KorGE
import korlibs.korge.view.Views

/** Config, repository (state dùng chung) và use case. Singleton trong một phiên chạy. */
fun Injector.coreModule(views: Views) {
  // Một GameConfig dùng chung cho cả game
  mapSingleton { GameConfig() }

  // Kho của KorGE: file trên desktop, localStorage trên web
  mapSingleton<KeyValueStorage> { KorgeKeyValueStorage(views.storage) }
  // Repository điểm cao nhất dùng kho ở trên
  mapSingleton<BestScoreRepository> { StoredBestScoreRepository(get()) }

  // Use case lấy điểm cao nhất
  mapSingleton { GetBestScoreUseCase(get()) }
  // Use case nộp điểm
  mapSingleton { SubmitScoreUseCase(get()) }

  // Tên người chơi do app chủ truyền vào (query string trên web, biến môi trường trên desktop)
  mapSingleton<PlayerRepository> { EnvironmentPlayerRepository() }
  // Use case lấy tên người chơi
  mapSingleton { GetPlayerNameUseCase(get()) }
}
