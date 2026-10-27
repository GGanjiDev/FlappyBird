/*
 * Copyright 2026 AppMb
 *
 */
// Hàm đăng ký toàn bộ DI + destination
import com.appmb.flappy.app.navigation.registerAppGraph
// Hàm mở màn hình đầu tiên
import com.appmb.flappy.app.navigation.startGame
// Bảng màu (lấy màu nền cửa sổ)
import com.appmb.flappy.core.base.ui.Palette
// Hàm Korge(...) khởi tạo engine
import korlibs.korge.*
// sceneContainer() và các API Scene
import korlibs.korge.scene.*
// Kiểu Size
import korlibs.math.geom.*

/**
 * Vỏ ứng dụng (điểm vào cho mọi nền tảng).
 * Giữ file này thật nhỏ: cấu hình cửa sổ, đăng ký graph, mở Scene đầu tiên.
 */
suspend fun main() = Korge(
  // Kích thước cửa sổ 800x600
  windowSize = Size(800, 600),
  // Màu nền cửa sổ
  backgroundColor = Palette.Background,
  // Thư mục cài đặt / kho lưu trữ riêng (mặc định sẽ dùng chung với mọi app KorGE)
  gameId = "com.appmb.flappy",
) {
  // Đăng ký DI và các màn hình
  injector.registerAppGraph(views)
  // Tạo bộ chứa Scene rồi mở màn hình game
  sceneContainer().startGame()
}
