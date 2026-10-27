/*
 * Copyright 2026 AppMb
 *
 */
// Package công khai của thư viện
package com.appmb.flappy.sdk

// Nền màu cho vùng chứa
import androidx.compose.foundation.background
// Hộp chứa căn giữa
import androidx.compose.foundation.layout.Box
// Chiếm toàn bộ kích thước
import androidx.compose.foundation.layout.fillMaxSize
// Hàm Composable
import androidx.compose.runtime.Composable
// Đọc giá trị State bằng `by`
import androidx.compose.runtime.getValue
// Tạo lại nội dung khi khoá thay đổi
import androidx.compose.runtime.key
// State nạp bất đồng bộ
import androidx.compose.runtime.produceState
// Modifier của Compose
import androidx.compose.ui.Modifier
// Màu
import androidx.compose.ui.graphics.Color
// Lớp Res (nội bộ) sinh từ composeResources
import com.appmb.flappy.sdk.resources.Res
// WebView đa nền tảng
import com.multiplatform.webview.web.WebView
// Tạo state WebView từ chuỗi HTML
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
// Dispatcher cho việc đọc file
import kotlinx.coroutines.Dispatchers
// Chuyển sang dispatcher khác
import kotlinx.coroutines.withContext
// API đọc composeResources còn thử nghiệm
import org.jetbrains.compose.resources.ExperimentalResourceApi

// Đường dẫn file game trong composeResources của thư viện
private const val GAME_HTML_PATH = "files/flappybird.html"

// Origin giả cho trang: cần một origin thật để localStorage (lưu best score) hoạt động
private const val GAME_BASE_URL = "https://localhost/"

// Tên query string mà game đọc (korlibs Environment["FLAPPY_USERNAME"], không phân biệt hoa thường)
private const val PLAYER_NAME_PARAM = "flappy_username"

// Màu nền giống viền ngoài canvas 800x600 của game, để hai bên liền màu
private val GameBackground = Color(0xFF2B2B2B)

/**
 * Trò chơi Flappy Bird. Chạm màn hình để vỗ cánh.
 *
 * @param playerName tên hiển thị "Welcome <playerName>" ở màn chờ; rỗng thì không chào.
 * @param modifier kích thước / vị trí của vùng game (game tự co giãn, giữ tỉ lệ 4:3).
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun FlappyBirdGame(
  playerName: String,
  modifier: Modifier = Modifier,
) {
  // Nạp HTML của game một lần (null trong lúc đang đọc)
  val html by produceState<String?>(initialValue = null) {
    // Đọc file ngoài main thread
    value = withContext(Dispatchers.Default) { Res.readBytes(GAME_HTML_PATH).decodeToString() }
  }

  // Vùng chứa có nền tối trong lúc chờ nạp
  Box(modifier = modifier.background(GameBackground)) {
    // Lấy giá trị hiện tại để smart cast
    val content = html
    // Đã nạp xong thì hiện WebView; đổi người chơi thì tạo WebView mới với URL mới
    if (content != null) {
      key(playerName) {
        GameWebView(html = content, baseUrl = gameUrl(playerName))
      }
    }
  }
}

// WebView chạy bản web của game
@Composable
private fun GameWebView(html: String, baseUrl: String) {
  // State chỉ nhớ nội dung lần đầu, nên chỉ tạo khi đã có HTML
  val state = rememberWebViewStateWithHTMLData(
    // Nội dung trang
    data = html,
    // URL của trang (mang theo tên người chơi trong query string)
    baseUrl = baseUrl,
    // Kiểu nội dung
    mimeType = "text/html",
  )
  // Android tắt localStorage mặc định; phải bật trước khi WebView được tạo
  state.webSettings.androidWebSettings.domStorageEnabled = true

  // Hiển thị WebView chiếm toàn bộ vùng chứa
  WebView(
    state = state,
    modifier = Modifier.fillMaxSize(),
    // Nút back thuộc về app chủ, không dùng để lùi trang trong WebView
    captureBackPresses = false,
  )
}

// URL của trang game: "https://localhost/?flappy_username=<tên đã mã hoá>"
internal fun gameUrl(playerName: String): String {
  // Bỏ khoảng trắng hai đầu
  val name = playerName.trim()
  // Không có tên thì không thêm query string
  return if (name.isEmpty()) GAME_BASE_URL else "$GAME_BASE_URL?$PLAYER_NAME_PARAM=${encodeQueryValue(name)}"
}

// Mã hoá phần trăm (percent-encoding) theo UTF-8, giữ nguyên ký tự an toàn A-Z a-z 0-9 - _ . ~
internal fun encodeQueryValue(value: String): String = buildString {
  // Duyệt từng byte UTF-8
  for (byte in value.encodeToByteArray()) {
    // Đổi sang số không dấu 0..255
    val code = byte.toInt() and 0xFF
    // Ký tự tương ứng
    val char = code.toChar()
    // Ký tự an toàn thì giữ nguyên
    if (char.isLetterOrDigit() && code < 0x80 || char in "-_.~") {
      append(char)
    } else {
      // Còn lại viết dạng %XX
      append('%')
      append(code.toString(16).uppercase().padStart(2, '0'))
    }
  }
}
