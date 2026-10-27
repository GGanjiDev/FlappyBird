# CLAUDE.md

Game Flappy Bird 2D viết bằng **KorGE 6.0.0 + Kotlin Multiplatform** (Kotlin 2.0.20).
Kiến trúc: **Clean Architecture + MVI + multi-module chia theo feature** (cùng convention với dự án HavestMoon).

Đọc `docs/ARCHITECTURE.md` trước khi tạo module hoặc màn hình mới.

---

## Commands

```bash
./gradlew runJvm                    # chạy game trên desktop
./gradlew jvmTest                   # chạy toàn bộ unit test của mọi module
./gradlew :feature:game:jvmTest     # test một module
./gradlew :core:domain:jvmTest      # test luật game (nhanh, không cần KorGE)
./gradlew browserReleaseWebpack     # bản web: build/www (CI deploy lên GitHub Pages)
```

**Bản web (GitHub Pages):** mỗi lần push vào `main`, workflow `.github/workflows/deploy-js.yml` build `browserReleaseWebpack`
và deploy `build/www` lên https://ggangjidev.github.io/FlappyBird/ (repo phải public, Pages Source = GitHub Actions).
App KMP-SevenBalls mở URL này trong WebView → **sửa game chỉ cần push, không cần phát hành lại app**.

- Tên người chơi: app chủ truyền qua query string `?flappy_username=...` → game đọc `Environment["FLAPPY_USERNAME"]`
  (`EnvironmentPlayerRepository`); desktop: `FLAPPY_USERNAME=ganji ./gradlew runJvm`.
- Input phải chạy được bằng chạm (`onDown`), vì điện thoại không có phím SPACE.
- Best score lưu trong `localStorage` của trang (WebView Android cần `domStorageEnabled`).
- Đường dẫn tài nguyên phải là tương đối (`resourcesVfs["bird_idle.png"]`): trang chạy dưới `/FlappyBird/`, không ở gốc domain.

JDK 21+ (CI dùng 21). Giữ style theo `.editorconfig` (Kotlin indent 2).

---

## Module map

```
(root)          app shell: src/main.kt, resources/ (bird_idle/up/down.png)
app/navigation  AppNavGraph (đăng ký DI + destination) + di/CoreModule + di/KorgeKeyValueStorage
feature/*       game
core/*          model · domain · data · engine
core-base/*     common (DispatcherProvider) · mvi (BaseViewModel) · ui (Palette, label, collectState/collectEffects)
deps/           external kproject deps (deps.kproject.yml) — do KorGE tự sinh, KHÔNG sửa tay
```

Chiều phụ thuộc: `root → app → feature → core → core-base`. **Không bao giờ ngược lại. Feature không phụ thuộc feature khác** — giao tiếp qua repository/use case ở `core/domain`, hoặc callback do `app/navigation` nối.

---

## Quy ước bắt buộc

**MVI:** mỗi màn hình có
- `XxxContract` — `sealed class Event`, `sealed class Effect`, `data class State` (bất biến, có default, không chứa object KorGE)
- `XxxViewModel : BaseViewModel<Event, State, Effect>` — `handleEvents()` chỉ dispatch, `setState { copy(...) }`, `setEffect { }`
- `XxxScene` (KorGE `Scene`) — chỉ build view, gửi Event, `collectState` → render, `collectEffects`. Gọi `viewModel.clear()` trong `sceneDestroy()`.
- `XxxRoute.kt` — `fun Injector.xxxDestination(...)` + `suspend fun SceneContainer.navigateToXxx()`
- `di/XxxModule.kt` — `fun Injector.xxxModule()` (DI = `korlibs.inject.Injector`)

**Real-time vs MVI:** vị trí chim, ống, timer vỗ cánh là state của engine (`feature/game/world/FlappyWorld`, Kotlin thuần), không nằm trong MVI State.
Scene chạy `sceneView.runSystems(views.input.keys, systems)`; world chỉ gửi Event khi có **thay đổi có ý nghĩa** (qua ống, va chạm, chạm đất) — **không gửi Event mỗi frame**.
`GamePhase` (READY/PLAYING/DEAD) nằm trong MVI State = nguồn sự thật duy nhất; world đọc phase mỗi frame và nhận lệnh qua Effect (`StartRun`, `Flap`, `ResetWorld`).

**Luật game là hàm thuần** trong `core/domain/rules` (`BirdPhysics`, `PipeRules`, `WingRules`, `ScoreRules`) — không KorGE, không IO, random được truyền vào; có test.

**Hằng số cân bằng game** nằm trong `core/model/config/GameConfig` (đăng ký singleton trong `CoreModule`).

**Dependency:** khai báo qua `gradle/libs.versions.toml` (giữ `[plugins]` là section CUỐI — `settings.gradle.kts` parse file này để tìm version KorGE). Thư viện mới phải hỗ trợ mọi target KorGE (JVM, JS, Android, iOS, tvOS).

**KorGE API:** thay đổi nhiều giữa các bản. Không đoán — tra source v6.0.0 hoặc docs.korge.org.
- Trong updater của một View thường, `keys` là DSL `KeysEvents`, không phải `InputKeys` → dùng `views.input.keys`.
- `changeTo<T>()` cần `mapPrototype { T(...) }` trước (làm trong `XxxRoute.kt`).

**Code style:** indent 2, trailing comma, header `Copyright 2026 AppMb`, test đặt tên bằng backtick mô tả hành vi.

---

## Quy trình làm việc

- Chỉ báo "xong" khi `./gradlew jvmTest` pass và game chạy được bằng `./gradlew runJvm`.
- Không commit trực tiếp vào `main`.
