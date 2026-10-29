# Flappy Bird (KorGE)

Flappy Bird viết bằng [KorGE](https://docs.korge.org/korge/) 6.0.0, kiến trúc Clean Architecture + MVI + multi-module.

Chơi trên web: https://gganjidev.github.io/FlappyBird/ (thêm `?flappy_username=<tên>` để hiện lời chào).

```bash
./gradlew runJvm     # chơi: SPACE (hoặc chạm) để bắt đầu / vỗ cánh / chơi lại
./gradlew jvmTest
```

---

## Architecture

Clean Architecture, MVI (`Contract` / `BaseViewModel` / Effect), multi-module theo feature.
Viết lại từ bản một-file `src/main.kt` (korge-hello-world) theo convention của dự án HavestMoon.

| App Android thường | game này | Lý do |
|---|---|---|
| Compose `@Composable` | KorGE `Scene` + `View` | engine game |
| androidx `ViewModel` | `BaseViewModel` tự quản lý scope, `clear()` trong `sceneDestroy()` | không có androidx trên mọi target |
| Koin | `korlibs.inject.Injector` (có sẵn trong KorGE) | KorGE tạo Scene qua Injector |
| build-logic convention plugins | plugin `com.soywiz.kproject` + block `subprojects {}` ở root | cách KorGE dựng library module |
| Navigation Compose | `SceneContainer.changeTo<T>()` bọc trong `XxxRoute.kt` | |

---

### 1. Modules

```
FlappyBird/
├── build.gradle.kts            # app shell (plugin com.soywiz.korge) + cấu hình chung cho subprojects
├── settings.gradle.kts         # include mọi module
├── src/main.kt                 # Korge(gameId=…) { injector.registerAppGraph(views); sceneContainer().startGame() }
├── resources/                  # bird_idle.png, bird_up.png, bird_down.png, flap.wav, music.mp3
├── app/navigation/             # AppNavGraph.kt + di/CoreModule.kt + di/KorgeKeyValueStorage.kt
├── feature/
│   └── game/                   # presentation/ (Contract, ViewModel, Scene, Route) · world/FlappyWorld · di/
├── core/
│   ├── model/                  # GameConfig, Bird, Pipe, GamePhase, WingFrame (pure)
│   ├── domain/                 # rules/ (hàm thuần) · usecase/ · repository/ (interface)
│   ├── data/                   # StoredBestScoreRepository, InMemoryKeyValueStorage
│   └── engine/                 # GameSystem + runSystems, ActionInput / InputBindings
├── core-base/
│   ├── common/                 # DispatcherProvider
│   ├── mvi/                    # BaseViewModel, ViewEvent/ViewState/ViewSideEffect
│   └── ui/                     # Palette, TextSize, label/centeredLabel, collectState/collectEffects
└── deps/                       # do KorGE sinh từ deps.kproject.yml — không sửa
```

| Module | Phụ thuộc | KorGE? |
|---|---|---|
| core-base/common | coroutines | không |
| core-base/mvi | common | không |
| core-base/ui | mvi, korge | có |
| core/model | — | không |
| core/domain | model, common | **không bao giờ** |
| core/data | domain | không |
| core/engine | model, korge | có |
| feature/* | ui, domain, engine | có |
| app/navigation | data + mọi feature | có |

---

### 2. MVI trên KorGE

```
 SPACE ─► ActionInput ─► viewModel.setEvent(OnFlapPressed)
                                │ handleEvents (đồng bộ)
                                ▼
   State(phase, score, best…) ◄─┤──► Effect(StartRun / Flap / ResetWorld) ─► FlappyWorld
          │                     ▲                                               │ update(dt, state.phase)
          ▼                     └── OnPipePassed / OnCrashed / OnBirdLanded ◄───┘ (chỉ khi có sự kiện)
   Scene.render: score, message                    Scene vẽ bird/pipes từ FlappyWorld mỗi frame
```

- `setEvent` gọi `handleEvents` **đồng bộ** → world đọc được phase mới ngay trong cùng frame.
- Kết thúc lượt (`OnCrashed`) gọi `SubmitScoreUseCase` → best score lưu qua `BestScoreRepository` (KorGE `views.storage`).
- Retry chỉ được khi chim đã nằm đất (`canRetry`, do `OnBirdLanded`).

### 3. Game loop

`sceneView.runSystems(views.input.keys, listOf(actionInput, worldSystem))`:
- READY: chim bập bềnh (`BirdPhysics.hoverY`), không sinh ống.
- PLAYING: trọng lực + nghiêng, chặn trần; sinh ống mỗi `spawnInterval` (ống đầu xuất hiện ngay), trôi trái, va chạm tròn–chữ nhật, +1 điểm khi mép phải ống qua chim, xoá ống ra khỏi màn hình.
- DEAD: ống đứng yên, chim rơi tới đất.
- Vỗ cánh: mỗi lần bấm 3 nhịp, mỗi nhịp lên → giữa → xuống → giữa (`WingRules`).

### 4. Testing

| Loại | Ở đâu | Cách |
|---|---|---|
| Luật game | `core/domain/src/commonTest` | `kotlin.test`, hàm thuần + fake repo |
| Lưu best score | `core/data/src/commonTest` | storage in-memory |
| World (real-time) | `feature/game/src/commonTest/FlappyWorldTest` | `Random` có seed, bước dt cố định |
| ViewModel | `feature/game/src/commonTest/GameViewModelTest` | `runTest` + `UnconfinedTestDispatcher` |
| Cảm giác chơi | `./gradlew runJvm` | thủ công |
