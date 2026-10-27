/*
 * Copyright 2026 AppMb
 *
 */
// Package test trùng package của use case
package com.appmb.flappy.core.domain.usecase

// Interface cần giả lập
import com.appmb.flappy.core.domain.repository.PlayerRepository
// Annotation đánh dấu hàm test
import kotlin.test.Test
// Các hàm kiểm tra kết quả
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PlayerUseCasesTest {
  // Tạo use case với một tên cố định
  private fun useCase(name: String?) = GetPlayerNameUseCase(object : PlayerRepository {
    // Luôn trả về tên đã cho
    override fun playerName(): String? = name
  })

  @Test
  fun `the name is trimmed`() {
    // Khoảng trắng hai đầu bị bỏ
    assertEquals("ganji", useCase("  ganji ")())
  }

  @Test
  fun `a missing or blank name means no greeting`() {
    // Không truyền tên
    assertNull(useCase(null)())
    // Tên toàn khoảng trắng
    assertNull(useCase("   ")())
  }

  @Test
  fun `a long name is cut`() {
    // Tên 30 ký tự bị cắt còn 20
    assertEquals(GetPlayerNameUseCase.MAX_LENGTH, useCase("a".repeat(30))()?.length)
  }
}
