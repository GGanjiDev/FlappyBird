/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.core.engine

import korlibs.korge.input.InputKeys
import korlibs.korge.view.View
import korlibs.korge.view.addUpdater
import kotlin.time.Duration

/**
 * Thứ chạy mỗi frame (vật lý, hoạt ảnh, bộ đếm thời gian...).
 *
 * Quy tắc: một system KHÔNG được gửi Event MVI mỗi frame. Nó chỉ gọi lại khi có điều
 * đáng kể xảy ra (bấm phím, qua ống, va chạm).
 */
fun interface GameSystem {
  fun update(keys: InputKeys, dt: Duration)
}

/**
 * Chạy [systems] theo thứ tự mỗi frame khi view này còn trên màn hình.
 * Gắn vào `sceneView` của Scene để nó tự dừng cùng Scene, và truyền `views.input.keys`
 * (trong một view không phải Stage, `keys` là DSL KeysEvents chứ không phải InputKeys).
 */
fun View.runSystems(input: InputKeys, systems: List<GameSystem>) = addUpdater { dt ->
  for (i in systems.indices) systems[i].update(input, dt)
}
