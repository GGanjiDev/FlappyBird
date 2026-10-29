/*
 * Copyright 2026 AppMb
 *
 */
import com.appmb.flappy.app.navigation.registerAppGraph
import com.appmb.flappy.app.navigation.startGame
import com.appmb.flappy.core.base.ui.Palette
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.math.geom.*

/**
 * Vỏ ứng dụng (điểm vào cho mọi nền tảng).
 * Giữ file này thật nhỏ: cấu hình cửa sổ, đăng ký graph, mở Scene đầu tiên.
 */
suspend fun main() = Korge(
  windowSize = Size(800, 600),
  backgroundColor = Palette.Background,
  gameId = "com.appmb.flappy",
) {
  injector.registerAppGraph(views)
  sceneContainer().startGame()
}
