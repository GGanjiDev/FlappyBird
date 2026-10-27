// Import API cấu hình của plugin KorGE
import korlibs.korge.gradle.*

plugins {
  // Plugin KorGE cho app (version lấy từ gradle/libs.versions.toml)
  alias(libs.plugins.korge)
}

// Cấu hình ứng dụng KorGE
korge {
  // Mã định danh ứng dụng (package Android / bundle iOS)
  id = "com.appmb.flappy"

  // Desktop chạy bằng JVM
  targetJvm()
  // Trình duyệt (JavaScript)
  targetJs()
  // Desktop native
  targetDesktop()
  // iOS
  targetIos()
  // Android
  targetAndroid()
}

dependencies {
  // Thư viện ngoài khai báo trong deps.kproject.yml
  add("commonMainApi", project(":deps"))
  // Navigation graph kéo theo (bắc cầu) mọi module feature + core.
  add("commonMainApi", project(":app:navigation"))
}

// Kiểm tra phụ thuộc task chặt chẽ của Gradle 8.14 làm build lỗi ở các task lint release
// Android của KorGE (lintVital*), vì chúng đọc thư mục processedResources JVM dùng chung mà
// không khai báo phụ thuộc. Không cần lint release để build app nên tắt các task này.
tasks.matching { it.name.startsWith("lintVital") }.configureEach { enabled = false }

// ---------------------------------------------------------------------------
// Cấu hình dùng chung cho mọi library module kproject
// ---------------------------------------------------------------------------
subprojects {
  // Chỉ áp dụng cho module có plugin Android library
  plugins.withId("com.android.library") {
    // Sửa lỗi KorGE/kproject 6.0.0: jvmTarget của Kotlin Android là 21 nhưng task biên dịch
    // Java vẫn giữ mặc định của AGP (1.8) -> lỗi "Inconsistent JVM-target".
    extensions.getByName("android").withGroovyBuilder {
      "compileOptions" {
        // Mã nguồn Java theo chuẩn 21
        setProperty("sourceCompatibility", JavaVersion.VERSION_21)
        // Bytecode Java theo chuẩn 21
        setProperty("targetCompatibility", JavaVersion.VERSION_21)
      }
    }
    // Namespace: kproject tự đặt duy nhất từ tên project ("FlappyBird.<tên>").
    // Đừng ghi đè trong afterEvaluate ("It is too late to set namespace").
  }
}

