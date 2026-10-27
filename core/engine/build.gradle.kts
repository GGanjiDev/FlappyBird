plugins {
  // Plugin của KorGE để dựng library module đa nền tảng
  id("com.soywiz.kproject")
}

// Tiện ích vòng lặp game thời gian thực (system, input). Phụ thuộc KorGE.
dependencies {
  // Model của game
  add("commonMainApi", project(":core:model"))
  // KorGE (View, InputKeys, Key)
  add("commonMainApi", libs.korge)
}
