// Import cấu hình jvmTarget của Kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  // Thư viện Android (sinh file .aar)
  alias(libs.plugins.androidLibrary)
  // Kotlin Multiplatform (Android + iOS + desktop)
  alias(libs.plugins.kotlinMultiplatform)
  // Compose Multiplatform (UI + resources)
  alias(libs.plugins.jetbrainsCompose)
  // Trình biên dịch Compose
  alias(libs.plugins.composeCompiler)
  // Publish thư viện ra Maven repository
  `maven-publish`
}

// Toạ độ Maven: com.appmb.flappy:flappybird:<version>
group = "com.appmb.flappy"
version = providers.gradleProperty("flappy.version").getOrElse("1.0.0")

kotlin {
  // Không publish source jar (chỉ phục vụ "go to source" trong IDE) để repo gọn hơn
  withSourcesJar(publish = false)

  androidTarget {
    // Chỉ publish bản release (file .aar)
    publishLibraryVariants("release")
    // Bytecode Java 17 giống app chủ
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
  }
  // iPhone thật
  iosArm64()
  // iOS Simulator trên Mac Apple Silicon
  iosSimulatorArm64()
  // Chỉ Android + iOS: không có desktop/web (app chủ tự hiển thị chỗ trống trên desktop)

  sourceSets {
    commonMain.dependencies {
      // Runtime Compose
      implementation(compose.runtime)
      // Layout cơ bản (Box, background...)
      implementation(compose.foundation)
      // Đọc file trong composeResources (Res.readBytes)
      implementation(compose.components.resources)
      // WebView đa nền tảng để chạy bản web của game
      implementation(libs.compose.webview.multiplatform)
    }
    commonTest.dependencies {
      // kotlin.test cho mọi target
      implementation(kotlin("test"))
    }
  }
}

android {
  // Namespace của thư viện Android
  namespace = "com.appmb.flappy.sdk"
  // SDK biên dịch
  compileSdk = libs.versions.android.compileSdk.get().toInt()
  defaultConfig {
    // Android thấp nhất hỗ trợ (giống app chủ)
    minSdk = libs.versions.android.minSdk.get().toInt()
  }
  compileOptions {
    // Java 17 giống app chủ
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

// ---------------------------------------------------------------------------
// File game: lấy build/single/flappybird.html của build KorGE ở thư mục gốc
// (tạo bằng `./gradlew packSingleHtml` ở thư mục gốc FlappyBird)
// ---------------------------------------------------------------------------
// File HTML game do build KorGE sinh ra
val gameHtml = rootDir.resolve("../build/single/flappybird.html")
// Thư mục composeResources mặc định của commonMain (file game copy vào đây được .gitignore bỏ qua)
val gameResourcesDir = layout.projectDirectory.dir("src/commonMain/composeResources")

// Copy file HTML game vào composeResources/files/
val syncGameHtml by tasks.registering(Sync::class) {
  // Nhóm hiển thị trong `./gradlew tasks`
  group = "build"
  // Mô tả task
  description = "Copy bản web 1 file của game vào composeResources của thư viện"
  doFirst {
    // Báo lỗi rõ ràng nếu chưa build bản web
    check(gameHtml.exists()) { "Missing $gameHtml. Run `./gradlew packSingleHtml` in the FlappyBird root first." }
  }
  // Nguồn: file HTML game
  from(gameHtml)
  // Đích: src/commonMain/composeResources/files/flappybird.html
  into(gameResourcesDir.dir("files"))
  // Giữ file .gitkeep: plugin Compose chỉ nhận thư mục composeResources nếu nó tồn tại lúc cấu hình
  preserve { include(".gitkeep") }
}

compose.resources {
  // Lớp Res chỉ dùng nội bộ thư viện
  publicResClass = false
  // Package của lớp Res sinh ra (tránh trùng với Res của app chủ)
  packageOfResClass = "com.appmb.flappy.sdk.resources"
  // Luôn sinh lớp Res
  generateResClass = always
}

// Mọi task đọc composeResources phải chạy sau khi file game được copy xong
tasks.configureEach {
  // Tên task có chữ "Resources" (prepareComposeResources..., copyNonXmlValueResources..., ...), trừ chính syncGameHtml
  if (name != syncGameHtml.name && name.contains("Resources")) dependsOn(syncGameHtml)
}

publishing {
  repositories {
    maven {
      // Tên repository -> task publishAllPublicationsToFlappyRepository
      name = "flappy"
      // Thư mục đích: -Pflappy.repo=<đường dẫn> (vd maven_local của SevenBalls), mặc định build/repo
      url = uri(providers.gradleProperty("flappy.repo").getOrElse(layout.buildDirectory.dir("repo").get().asFile.path))
    }
  }
}

