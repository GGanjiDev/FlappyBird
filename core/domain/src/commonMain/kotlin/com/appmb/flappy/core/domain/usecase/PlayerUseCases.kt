/*
 * Copyright 2026 AppMb
 *
 */
// Package use case của module core/domain
package com.appmb.flappy.core.domain.usecase

// Interface nguồn thông tin người chơi
import com.appmb.flappy.core.domain.repository.PlayerRepository

/** Use case: lấy tên người chơi để chào; tên rỗng hoặc toàn khoảng trắng coi như không có. */
class GetPlayerNameUseCase(private val repository: PlayerRepository) {
  // Cho phép gọi như hàm: getPlayerName()
  operator fun invoke(): String? = repository.playerName()
    // Bỏ khoảng trắng hai đầu
    ?.trim()
    // Cắt bớt tên quá dài để không tràn màn hình
    ?.take(MAX_LENGTH)
    // Chuỗi rỗng thì trả null
    ?.takeIf { it.isNotEmpty() }

  companion object {
    // Số ký tự tối đa hiển thị
    const val MAX_LENGTH = 20
  }
}
