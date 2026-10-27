/*
 * Copyright 2026 AppMb
 *
 */
// Package test của feature game
package com.appmb.flappy.feature.game

// Interface dispatcher
import com.appmb.flappy.core.base.common.DispatcherProvider
// Kho key/value trong bộ nhớ
import com.appmb.flappy.core.data.repository.InMemoryKeyValueStorage
// Repository điểm cao nhất thật (chạy trên kho trong bộ nhớ)
import com.appmb.flappy.core.data.repository.StoredBestScoreRepository
// Interface nguồn thông tin người chơi
import com.appmb.flappy.core.domain.repository.PlayerRepository
// Use case lấy điểm cao nhất
import com.appmb.flappy.core.domain.usecase.GetBestScoreUseCase
// Use case lấy tên người chơi
import com.appmb.flappy.core.domain.usecase.GetPlayerNameUseCase
// Use case nộp điểm
import com.appmb.flappy.core.domain.usecase.SubmitScoreUseCase
// Các pha của lượt chơi
import com.appmb.flappy.core.model.world.GamePhase
// Effect của màn hình game
import com.appmb.flappy.feature.game.presentation.GameContract.Effect
// Event của màn hình game
import com.appmb.flappy.feature.game.presentation.GameContract.Event
// ViewModel cần test
import com.appmb.flappy.feature.game.presentation.GameViewModel
// Kiểu dispatcher
import kotlinx.coroutines.CoroutineDispatcher
// Cho phép API thử nghiệm của coroutines-test
import kotlinx.coroutines.ExperimentalCoroutinesApi
// Lấy phần tử đầu của Flow
import kotlinx.coroutines.flow.first
// Lấy n phần tử đầu của Flow
import kotlinx.coroutines.flow.take
// Gom Flow thành List
import kotlinx.coroutines.flow.toList
// Scope của test coroutine
import kotlinx.coroutines.test.TestScope
// Dispatcher chạy ngay trong test
import kotlinx.coroutines.test.UnconfinedTestDispatcher
// Chạy test có coroutine
import kotlinx.coroutines.test.runTest
// Annotation đánh dấu hàm test
import kotlin.test.Test
// Các hàm kiểm tra kết quả
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Cho phép API thử nghiệm trong cả lớp test
@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

  // Repository dùng chung cho một test
  private val repository = StoredBestScoreRepository(InMemoryKeyValueStorage())

  // Tạo ViewModel với use case thật và dispatcher của test; [playerName] giả lập tên app chủ truyền vào
  private fun TestScope.viewModel(playerName: String? = null) = GameViewModel(
    getBestScore = GetBestScoreUseCase(repository),
    submitScore = SubmitScoreUseCase(repository),
    getPlayerName = GetPlayerNameUseCase(object : PlayerRepository {
      // Luôn trả về tên đã cho
      override fun playerName(): String? = playerName
    }),
    dispatchers = object : DispatcherProvider {
      // Dispatcher chạy ngay, dùng scheduler của test
      override val default: CoroutineDispatcher = UnconfinedTestDispatcher(testScheduler)
    },
  )

  @Test
  fun `starts ready with the stored best`() = runTest {
    // Kỷ lục đã lưu là 7
    repository.setBest(7)
    // Tạo ViewModel
    val vm = viewModel()

    // Bắt đầu ở pha chờ
    assertEquals(GamePhase.READY, vm.state.value.phase)
    // Đã nạp kỷ lục 7
    assertEquals(7, vm.state.value.best)
    // Thông báo hướng dẫn
    assertEquals("Tap or press SPACE", vm.state.value.message)
    // Dọn dẹp
    vm.clear()
  }

  @Test
  fun `first press starts the run, next presses flap`() = runTest {
    // Tạo ViewModel
    val vm = viewModel()

    // Bấm lần 1: bắt đầu
    vm.setEvent(Event.OnFlapPressed)
    // Bấm lần 2: vỗ cánh
    vm.setEvent(Event.OnFlapPressed)

    // Đang chơi
    assertEquals(GamePhase.PLAYING, vm.state.value.phase)
    // Hai effect theo đúng thứ tự
    assertEquals(listOf(Effect.StartRun, Effect.Flap), vm.effect.take(2).toList())
    // Dọn dẹp
    vm.clear()
  }

  @Test
  fun `passing pipes scores only while playing`() = runTest {
    // Tạo ViewModel
    val vm = viewModel()
    // Qua ống khi chưa chơi
    vm.setEvent(Event.OnPipePassed)
    // Không được điểm
    assertEquals(0, vm.state.value.score)

    // Bắt đầu chơi
    vm.setEvent(Event.OnFlapPressed)
    // Qua hai ống
    vm.setEvent(Event.OnPipePassed)
    vm.setEvent(Event.OnPipePassed)

    // Được 2 điểm
    assertEquals(2, vm.state.value.score)
    // Dọn dẹp
    vm.clear()
  }

  @Test
  fun `crashing ends the run and records a new best`() = runTest {
    // Kỷ lục cũ là 1
    repository.setBest(1)
    // Tạo ViewModel
    val vm = viewModel()
    // Bắt đầu chơi
    vm.setEvent(Event.OnFlapPressed)
    // Qua 3 ống
    repeat(3) { vm.setEvent(Event.OnPipePassed) }

    // Va chạm
    vm.setEvent(Event.OnCrashed)
    // Bị bỏ qua vì đã chết
    vm.setEvent(Event.OnPipePassed)

    // Lấy State cuối
    val state = vm.state.value
    // Đã chết
    assertEquals(GamePhase.DEAD, state.phase)
    // Điểm giữ nguyên 3
    assertEquals(3, state.score)
    // Kỷ lục mới là 3
    assertEquals(3, state.best)
    // Có cờ kỷ lục mới
    assertTrue(state.isNewBest)
    // Đã lưu vào repository
    assertEquals(3, repository.best.value)
    // Dọn dẹp
    vm.clear()
  }

  @Test
  fun `retry is possible only once the bird has landed`() = runTest {
    // Tạo ViewModel
    val vm = viewModel()
    // Bắt đầu, qua 1 ống, rồi chết
    vm.setEvent(Event.OnFlapPressed)
    vm.setEvent(Event.OnPipePassed)
    vm.setEvent(Event.OnCrashed)
    // Lấy effect StartRun ra khỏi kênh
    assertEquals(Effect.StartRun, vm.effect.first())

    // Bấm khi chim còn đang rơi
    vm.setEvent(Event.OnFlapPressed)
    // Vẫn chết, chưa chơi lại được
    assertEquals(GamePhase.DEAD, vm.state.value.phase)
    assertFalse(vm.state.value.canRetry)

    // Chim nằm đất
    vm.setEvent(Event.OnBirdLanded)
    // Bấm chơi lại
    vm.setEvent(Event.OnFlapPressed)

    // Lấy State cuối
    val state = vm.state.value
    // Về pha chờ
    assertEquals(GamePhase.READY, state.phase)
    // Điểm về 0
    assertEquals(0, state.score)
    // Kỷ lục 1 được giữ lại
    assertEquals(1, state.best)
    // Có effect đặt lại world
    assertEquals(Effect.ResetWorld, vm.effect.first())
    // Dọn dẹp
    vm.clear()
  }

  @Test
  fun `game over message shows the best score`() = runTest {
    // Kỷ lục cũ là 10
    repository.setBest(10)
    // Tạo ViewModel
    val vm = viewModel()
    // Bắt đầu rồi chết ngay (0 điểm)
    vm.setEvent(Event.OnFlapPressed)
    vm.setEvent(Event.OnCrashed)

    // Thông báo hiện kỷ lục 10
    assertEquals("Game over - Best: 10 - tap to retry", vm.state.value.message)
    // Dọn dẹp
    vm.clear()
  }

  @Test
  fun `the player is welcomed by name while waiting`() = runTest {
    // App chủ truyền tên "ganji"
    val vm = viewModel(playerName = "ganji")

    // Màn chờ hiện lời chào
    assertEquals("Welcome ganji", vm.state.value.greeting)
    // Bắt đầu chơi thì ẩn lời chào
    vm.setEvent(Event.OnFlapPressed)
    assertEquals("", vm.state.value.greeting)
    // Chết, nằm đất, chơi lại: lời chào quay lại
    vm.setEvent(Event.OnCrashed)
    vm.setEvent(Event.OnBirdLanded)
    vm.setEvent(Event.OnFlapPressed)
    assertEquals("Welcome ganji", vm.state.value.greeting)
    // Dọn dẹp
    vm.clear()
  }

  @Test
  fun `no greeting without a player name`() = runTest {
    // Không có tên
    val vm = viewModel()

    // Không có lời chào
    assertEquals("", vm.state.value.greeting)
    // Dọn dẹp
    vm.clear()
  }
}
