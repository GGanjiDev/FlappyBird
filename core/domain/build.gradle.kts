plugins {
  id("com.soywiz.kproject")
}

// Kotlin thuần: luật game, use case và interface repository. KHÔNG BAO GIỜ phụ thuộc KorGE ở đây.
dependencies {
  add("commonMainApi", project(":core:model"))
  add("commonMainApi", project(":core-base:common"))
  add("commonMainApi", libs.kotlinx.coroutines.core)
  add("commonTestImplementation", libs.kotlinx.coroutines.test)
}
