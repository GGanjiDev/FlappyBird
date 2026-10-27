plugins {
  // Plugin của KorGE để dựng library module đa nền tảng
  id("com.soywiz.kproject")
}

// Kotlin thuần: luật game, use case và interface repository. KHÔNG BAO GIỜ phụ thuộc KorGE ở đây.
dependencies {
  // Model của game
  add("commonMainApi", project(":core:model"))
  // Tiện ích chung
  add("commonMainApi", project(":core-base:common"))
  // StateFlow trong interface repository
  add("commonMainApi", libs.kotlinx.coroutines.core)
  // Công cụ test coroutine
  add("commonTestImplementation", libs.kotlinx.coroutines.test)
}
