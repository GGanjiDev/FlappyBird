plugins {
  id("com.soywiz.kproject")
}

// Sở hữu navigation graph + nối DI. Module duy nhất được biết mọi feature.
dependencies {
  add("commonMainApi", project(":core:data"))
  add("commonMainApi", project(":feature:game"))
  add("commonMainApi", libs.korge)
}
