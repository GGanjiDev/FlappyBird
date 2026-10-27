/*
 * Copyright 2026 AppMb
 *
 */
// Package của module core/engine
package com.appmb.flappy.core.engine

// Trạng thái bàn phím của KorGE (phím nào đang giữ / vừa bấm)
import korlibs.korge.input.InputKeys
// Lớp View cơ sở của KorGE
import korlibs.korge.view.View
// Hàm gắn một hàm chạy mỗi frame vào View
import korlibs.korge.view.addUpdater
// Kiểu khoảng thời gian (thời gian giữa hai frame)
import kotlin.time.Duration

/**
 * Thứ chạy mỗi frame (vật lý, hoạt ảnh, bộ đếm thời gian...).
 *
 * Quy tắc: một system KHÔNG được gửi Event MVI mỗi frame. Nó chỉ gọi lại khi có điều
 * đáng kể xảy ra (bấm phím, qua ống, va chạm).
 */
fun interface GameSystem {
  // Được gọi mỗi frame với bàn phím và thời gian trôi qua
  fun update(keys: InputKeys, dt: Duration)
}

/**
 * Chạy [systems] theo thứ tự mỗi frame khi view này còn trên màn hình.
 * Gắn vào `sceneView` của Scene để nó tự dừng cùng Scene, và truyền `views.input.keys`
 * (trong một view không phải Stage, `keys` là DSL KeysEvents chứ không phải InputKeys).
 */
fun View.runSystems(input: InputKeys, systems: List<GameSystem>) = addUpdater { dt ->
  // Duyệt bằng chỉ số để không tạo iterator mỗi frame
  for (i in systems.indices) systems[i].update(input, dt)
}
