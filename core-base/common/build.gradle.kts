plugins {
  id("com.soywiz.kproject")
}

// Kotlin thuần: không có KorGE ở đây.
dependencies {
  add("commonMainApi", libs.kotlinx.coroutines.core)
}
