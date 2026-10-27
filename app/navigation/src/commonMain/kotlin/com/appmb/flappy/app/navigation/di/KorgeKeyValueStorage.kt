/*
 * Copyright 2026 AppMb
 *
 */
// Package DI của module app/navigation
package com.appmb.flappy.app.navigation.di

// Interface kho key/value (từ domain)
import com.appmb.flappy.core.domain.repository.KeyValueStorage
// Kho lưu trữ gốc của KorGE
import korlibs.korge.service.storage.NativeStorage

/**
 * [KeyValueStorage] dựa trên kho key/value của KorGE (JVM: file properties trong thư mục
 * preferences của app, ghi mỗi lần set; trình duyệt: localStorage). Key được thêm tiền tố.
 */
class KorgeKeyValueStorage(private val storage: NativeStorage) : KeyValueStorage {
  // Đọc theo key có tiền tố, null nếu chưa có
  override fun read(key: String): String? = storage.getOrNull(prefixed(key))
  override fun write(key: String, value: String) {
    // Ghi theo key có tiền tố
    storage[prefixed(key)] = value
  }

  // Thêm tiền tố "flappy." để không đụng key của thứ khác
  private fun prefixed(key: String) = "flappy.$key"
}
