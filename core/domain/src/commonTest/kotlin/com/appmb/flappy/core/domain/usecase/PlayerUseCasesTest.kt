/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.domain.usecase

import com.appmb.flappy.core.domain.repository.PlayerRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PlayerUseCasesTest {
  private fun useCase(name: String?) = GetPlayerNameUseCase(object : PlayerRepository {
    override fun playerName(): String? = name
  })

  @Test
  fun `the name is trimmed`() {
    assertEquals("ganji", useCase("  ganji ")())
  }

  @Test
  fun `a missing or blank name means no greeting`() {
    assertNull(useCase(null)())
    assertNull(useCase("   ")())
  }

  @Test
  fun `a long name is cut`() {
    assertEquals(GetPlayerNameUseCase.MAX_LENGTH, useCase("a".repeat(30))()?.length)
  }
}
