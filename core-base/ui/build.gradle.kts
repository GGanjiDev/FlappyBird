plugins {
  // Plugin của KorGE để dựng library module đa nền tảng
  id("com.soywiz.kproject")
}

// Bộ UI KorGE: bảng màu, nhãn chữ, hàm nối State/Effect cho Scene.
dependencies {
  // Dùng lại phần MVI
  add("commonMainApi", project(":core-base:mvi"))
  // Thư viện KorGE (View, Text, màu...)
  add("commonMainApi", libs.korge)
}
