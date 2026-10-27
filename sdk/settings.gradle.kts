// Gradle build RIÊNG cho thư viện nhúng game vào app khác (vd KMP-SevenBalls).
// Tách khỏi build KorGE ở thư mục gốc vì KorGE 6 cần Kotlin 2.0.20 / Gradle 8.8,
// còn app chủ dùng Kotlin 2.3 / Compose Multiplatform 1.9 / AGP 8.13 (Gradle 8.14).

// Tên build
rootProject.name = "flappybird-sdk"

// Nơi tìm plugin Gradle
pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

// Nơi tìm thư viện
dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    // Thư viện native của WebView desktop (KCEF) do compose-webview-multiplatform kéo theo
    maven("https://jogamp.org/deployment/maven/")
  }
}

// Module thư viện duy nhất
include(":flappybird")
