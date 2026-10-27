plugins {
  // Plugin của KorGE để dựng library module đa nền tảng
  id("com.soywiz.kproject")
}

// Nền tảng MVI Kotlin thuần (BaseViewModel không dùng androidx).
dependencies {
  // Dùng DispatcherProvider
  add("commonMainApi", project(":core-base:common"))
  // StateFlow, Channel, CoroutineScope
  add("commonMainApi", libs.kotlinx.coroutines.core)
  // runTest, UnconfinedTestDispatcher cho test
  add("commonTestImplementation", libs.kotlinx.coroutines.test)
}
