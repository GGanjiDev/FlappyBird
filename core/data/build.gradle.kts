plugins {
  id("com.soywiz.kproject")
}

// Cài đặt repository.
dependencies {
  add("commonMainApi", project(":core:domain"))
  add("commonTestImplementation", libs.kotlinx.coroutines.test)
}
