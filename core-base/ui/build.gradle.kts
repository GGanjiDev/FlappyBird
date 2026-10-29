plugins {
  id("com.soywiz.kproject")
}

// Bộ UI KorGE: bảng màu, nhãn chữ, hàm nối State/Effect cho Scene.
dependencies {
  add("commonMainApi", project(":core-base:mvi"))
  add("commonMainApi", libs.korge)
}
