/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.app.navigation.di

import com.appmb.flappy.core.data.repository.StoredBestScoreRepository
import com.appmb.flappy.core.domain.repository.BestScoreRepository
import com.appmb.flappy.core.domain.repository.KeyValueStorage
import com.appmb.flappy.core.domain.repository.PlayerRepository
import com.appmb.flappy.core.domain.usecase.GetBestScoreUseCase
import com.appmb.flappy.core.domain.usecase.GetPlayerNameUseCase
import com.appmb.flappy.core.domain.usecase.SubmitScoreUseCase
import com.appmb.flappy.core.model.config.GameConfig
import korlibs.inject.Injector
import korlibs.korge.service.storage.storage
import korlibs.korge.view.Views

/** Config, repository (state dùng chung) và use case. Singleton trong một phiên chạy. */
/** Đăng ký singleton dùng chung: config, storage, repository và use case. */
fun Injector.coreModule(views: Views) {
  mapSingleton { GameConfig() }

  mapSingleton<KeyValueStorage> { KorgeKeyValueStorage(views.storage) }
  mapSingleton<BestScoreRepository> { StoredBestScoreRepository(get()) }

  mapSingleton { GetBestScoreUseCase(get()) }
  mapSingleton { SubmitScoreUseCase(get()) }

  mapSingleton<PlayerRepository> { EnvironmentPlayerRepository() }
  mapSingleton { GetPlayerNameUseCase(get()) }
}
