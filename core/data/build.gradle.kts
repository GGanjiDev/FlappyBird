plugins {
  // Plugin của KorGE để dựng library module đa nền tảng
  id("com.soywiz.kproject")
}

// Cài đặt repository.
dependencies {
  // Cài đặt interface của domain
  add("commonMainApi", project(":core:domain"))
  // Công cụ test coroutine
  add("commonTestImplementation", libs.kotlinx.coroutines.test)
}
