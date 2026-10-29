import korlibs.korge.gradle.*

plugins {
  alias(libs.plugins.korge)
}

korge {
  id = "com.appmb.flappy"

  targetJvm()
  targetJs()
  targetDesktop()
  targetIos()
  targetAndroid()
}

dependencies {
  add("commonMainApi", project(":deps"))
  // Navigation graph kéo theo mọi module feature + core.
  add("commonMainApi", project(":app:navigation"))
}

// Gradle 8.14 báo lỗi phụ thuộc ngầm ở các task lint release Android của KorGE (lintVital*).
// Không cần lint release để build app nên tắt đi.
tasks.matching { it.name.startsWith("lintVital") }.configureEach { enabled = false }

subprojects {
  plugins.withId("com.android.library") {
    // Sửa lỗi KorGE/kproject 6.0.0: Kotlin Android dùng jvmTarget 21 nhưng Java vẫn để mặc định 1.8
    // của AGP -> "Inconsistent JVM-target". Namespace do kproject tự đặt ("FlappyBird.<tên>"), đừng ghi đè.
    extensions.getByName("android").withGroovyBuilder {
      "compileOptions" {
        setProperty("sourceCompatibility", JavaVersion.VERSION_21)
        setProperty("targetCompatibility", JavaVersion.VERSION_21)
      }
    }
  }
}
