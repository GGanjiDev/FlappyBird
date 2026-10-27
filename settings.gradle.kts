// Nơi Gradle tìm plugin
pluginManagement {
    repositories { mavenLocal(); mavenCentral(); google(); gradlePluginPortal() }
}

// Chạy trước khi cấu hình settings: tìm version KorGE để nạp plugin settings tương ứng
buildscript {
    // Đọc nội dung file version catalog
    val libsTomlFile = File(this.sourceFile?.parentFile, "gradle/libs.versions.toml").readText()
    // Đã vào section [plugins] chưa
    var plugins = false
    // Version KorGE tìm được
    var version = ""
    // Duyệt từng dòng (đã bỏ khoảng trắng hai đầu)
    for (line in libsTomlFile.lines().map { it.trim() }) {
        // Bỏ qua dòng chú thích
        if (line.startsWith("#")) continue
        // Đánh dấu khi gặp section [plugins]
        if (line.startsWith("[plugins]")) plugins = true
        // Dòng `korge = { ... version = "x" }` trong [plugins]: lấy version
        if (plugins && line.startsWith("korge") && Regex("^korge\\s*=.*").containsMatchIn(line)) version = Regex("version\\s*=\\s*\"(.*?)\"").find(line)?.groupValues?.get(1) ?: error("Can't find korge version")
    }
    // Không tìm thấy thì báo lỗi ngay
    if (version.isEmpty()) error("Can't find korge version in $libsTomlFile")

    // Nơi tải plugin settings
    repositories { mavenLocal(); mavenCentral(); google(); gradlePluginPortal() }

    dependencies {
        // Plugin settings của KorGE đúng version tìm được
        classpath("com.soywiz.korge.settings:com.soywiz.korge.settings.gradle.plugin:$version")
    }
}

// Áp dụng plugin settings của KorGE (tự include :deps và các kproject dep)
apply(plugin = "com.soywiz.korge.settings")

// Tên project gốc
rootProject.name = "FlappyBird"

// ---------------------------------------------------------------------------
// Các module của project (xem docs/ARCHITECTURE.md)
// Plugin settings của KorGE chỉ tự include `:deps` và các kproject dep trong
// deps.kproject.yml. Module của mình phải include thủ công.
// Tên module phải duy nhất (dùng làm namespace Android "FlappyBird.<tên>").
// ---------------------------------------------------------------------------

// core-base: khối nền tảng, không gắn với game cụ thể
include(":core-base:common")
include(":core-base:mvi")
include(":core-base:ui")

// core: domain của game, dùng chung cho mọi feature
include(":core:model")
include(":core:domain")
include(":core:data")
include(":core:engine")

// feature: độc lập, không bao giờ phụ thuộc lẫn nhau
include(":feature:game")

// app: navigation graph (nối các feature). Project gốc là vỏ ứng dụng.
include(":app:navigation")
