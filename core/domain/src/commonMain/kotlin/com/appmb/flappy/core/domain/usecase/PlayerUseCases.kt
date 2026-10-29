/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.domain.usecase

import com.appmb.flappy.core.domain.repository.PlayerRepository

/** Use case: lấy tên người chơi để chào; tên rỗng hoặc toàn khoảng trắng coi như không có. */
/** Tên người chơi do app chủ truyền vào: bỏ khoảng trắng, cắt còn [MAX_LENGTH] ký tự, rỗng thì null. */
class GetPlayerNameUseCase(private val repository: PlayerRepository) {
  operator fun invoke(): String? = repository.playerName()
    ?.trim()
    ?.take(MAX_LENGTH)
    ?.takeIf { it.isNotEmpty() }

  companion object {
    const val MAX_LENGTH = 20
  }
}
