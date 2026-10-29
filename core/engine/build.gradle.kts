plugins {
  id("com.soywiz.kproject")
}

// Tiện ích vòng lặp game thời gian thực (system, input). Phụ thuộc KorGE.
dependencies {
  add("commonMainApi", project(":core:model"))
  add("commonMainApi", libs.korge)
}
