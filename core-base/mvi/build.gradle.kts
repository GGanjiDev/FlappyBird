plugins {
  id("com.soywiz.kproject")
}

// Nền tảng MVI Kotlin thuần (BaseViewModel không dùng androidx).
dependencies {
  add("commonMainApi", project(":core-base:common"))
  add("commonMainApi", libs.kotlinx.coroutines.core)
  add("commonTestImplementation", libs.kotlinx.coroutines.test)
}
