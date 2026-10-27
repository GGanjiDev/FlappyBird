plugins {
  // Plugin của KorGE để dựng library module đa nền tảng
  id("com.soywiz.kproject")
}

// Kotlin thuần: không có KorGE ở đây.
dependencies {
  // Coroutines (DispatcherProvider dùng CoroutineDispatcher)
  add("commonMainApi", libs.kotlinx.coroutines.core)
}
