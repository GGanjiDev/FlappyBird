plugins {
  id("com.soywiz.kproject")
}

// Module feature: có Contract / ViewModel / Scene / Route / di riêng. Không bao giờ phụ thuộc feature khác.
dependencies {
  add("commonMainApi", project(":core-base:ui"))
  add("commonMainApi", project(":core:domain"))
  add("commonMainApi", project(":core:engine"))
  add("commonMainApi", libs.korge)

  add("commonTestImplementation", project(":core:data"))
  add("commonTestImplementation", libs.kotlinx.coroutines.test)
}
