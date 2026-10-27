plugins {
  // Plugin của KorGE để dựng library module đa nền tảng
  id("com.soywiz.kproject")
}

// Module feature: có Contract / ViewModel / Scene / Route / di riêng. Không bao giờ phụ thuộc feature khác.
dependencies {
  // Bảng màu, nhãn, collectState/collectEffects (kèm MVI)
  add("commonMainApi", project(":core-base:ui"))
  // Luật game và use case
  add("commonMainApi", project(":core:domain"))
  // GameSystem, ActionInput
  add("commonMainApi", project(":core:engine"))
  // KorGE (Scene, View)
  add("commonMainApi", libs.korge)

  // Repository trong bộ nhớ dùng làm đồ giả cho test
  add("commonTestImplementation", project(":core:data"))
  // Công cụ test coroutine
  add("commonTestImplementation", libs.kotlinx.coroutines.test)
}
