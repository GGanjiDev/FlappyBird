plugins {
  // Plugin của KorGE để dựng library module đa nền tảng
  id("com.soywiz.kproject")
}

// Sở hữu navigation graph + nối DI. Module duy nhất được biết mọi feature.
dependencies {
  // Cài đặt repository
  add("commonMainApi", project(":core:data"))
  // Feature game
  add("commonMainApi", project(":feature:game"))
  // KorGE (Injector, SceneContainer, storage)
  add("commonMainApi", libs.korge)
}
