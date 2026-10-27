// Import API cấu hình của plugin KorGE
import korlibs.korge.gradle.*
// Mã hoá base64 để nhúng ảnh vào HTML
import java.util.Base64

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

// ---------------------------------------------------------------------------
// Bản web một file duy nhất (dùng để nhúng game vào WebView của app khác, vd KMP-SevenBalls)
// ---------------------------------------------------------------------------
// Vì sao cần: KorGE trên web tải ảnh bằng fetch(), mà fetch không đọc được file:// trong
// WebView Android/iOS. Task này gộp index.html + FlappyBird.js + ảnh PNG thành một file HTML,
// ảnh nằm sẵn trong file và một đoạn shim trả ảnh cho fetch(), nên không cần đọc file nào khác.
val packSingleHtml by tasks.registering {
  // Nhóm hiển thị trong `./gradlew tasks`
  group = "distribution"
  // Mô tả task
  description = "Đóng gói bản web thành build/single/flappybird.html (JS + ảnh nằm sẵn trong file)"
  // Cần bản web release trước
  dependsOn("browserReleaseWebpack")

  // Thư mục bản web do KorGE sinh ra
  val www = layout.buildDirectory.dir("www")
  // File kết quả
  val output = layout.buildDirectory.file("single/flappybird.html")
  // Khai báo input/output để Gradle bỏ qua khi không có gì thay đổi
  inputs.dir(www)
  outputs.file(output)

  doLast {
    // Thư mục www dạng File
    val dir = www.get().asFile
    // Toàn bộ mã JS của game
    val js = dir.resolve("FlappyBird.js").readText()
    // Phòng hờ: JS chứa "</script" sẽ cắt ngang thẻ <script> khi chèn vào HTML
    check("</script" !in js) { "FlappyBird.js contains </script, cannot inline it" }
    // Tên ảnh -> nội dung base64 (chỉ lấy các file .png, sắp xếp cho kết quả ổn định)
    val images = dir.listFiles { file -> file.extension == "png" }.orEmpty().sortedBy { it.name }
      .joinToString(",\n") { "    \"${it.name}\": \"${Base64.getEncoder().encodeToString(it.readBytes())}\"" }
    // Shim: fetch() tới một ảnh đã nhúng thì trả Response tạo từ dữ liệu base64, còn lại gọi fetch gốc
    val shim = """
      |(function () {
      |  // Ảnh đã nhúng: tên file -> base64
      |  var assets = {
      |$images
      |  };
      |  // Giữ lại fetch gốc của trình duyệt
      |  var originalFetch = window.fetch.bind(window);
      |  // Đổi chuỗi base64 thành mảng byte
      |  function toBytes(base64) {
      |    var binary = atob(base64);
      |    var bytes = new Uint8Array(binary.length);
      |    for (var i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
      |    return bytes;
      |  }
      |  window.fetch = function (input, init) {
      |    // URL của request (chuỗi hoặc đối tượng Request)
      |    var url = typeof input === "string" ? input : (input && input.url) || "";
      |    // Tên file cuối URL, bỏ query string
      |    var name = url.split("?")[0].split("/").pop();
      |    // Không phải ảnh đã nhúng: dùng fetch gốc
      |    if (!Object.prototype.hasOwnProperty.call(assets, name)) return originalFetch(input, init);
      |    // Phương thức HTTP (KorGE gửi HEAD để hỏi kích thước trước khi đọc)
      |    var method = ((init && init.method) || (input && input.method) || "GET").toUpperCase();
      |    // Nội dung ảnh
      |    var bytes = toBytes(assets[name]);
      |    // Trả lời giống một server tĩnh: 200 + Content-Length, HEAD thì không có body
      |    return Promise.resolve(new Response(method === "HEAD" ? null : bytes, {
      |      status: 200,
      |      headers: { "Content-Type": "image/png", "Content-Length": String(bytes.length) },
      |    }));
      |  };
      |})();
    """.trimMargin()
    // Thẻ script gốc trỏ tới file JS ngoài
    val externalScript = "<script type=\"text/javascript\" src=\"FlappyBird.js\" onload=\"preloader_complete()\"></script>"
    // HTML gốc do KorGE sinh
    val html = dir.resolve("index.html").readText()
    // Template KorGE đổi khác thì báo lỗi thay vì sinh file hỏng
    check(externalScript in html) { "index.html no longer references FlappyBird.js the expected way" }
    // Thay thẻ script ngoài bằng: shim + JS chèn thẳng + tắt màn hình loading
    val single = html.replace(externalScript, "<script>\n$shim\n</script>\n<script>\n$js\npreloader_complete();\n</script>")
    // Ghi file kết quả
    output.get().asFile.apply {
      // Tạo thư mục cha nếu chưa có
      parentFile.mkdirs()
      // Ghi nội dung
      writeText(single)
    }
  }
}
