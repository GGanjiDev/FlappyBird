/*
 * Copyright 2026 AppMb
 *
 */
package com.appmb.flappy.app.navigation.di

import com.appmb.flappy.core.domain.repository.KeyValueStorage
import korlibs.korge.service.storage.NativeStorage

/**
 * [KeyValueStorage] dựa trên kho key/value của KorGE (JVM: file properties trong thư mục
 * preferences của app, ghi mỗi lần set; trình duyệt: localStorage). Key được thêm tiền tố.
 */
class KorgeKeyValueStorage(private val storage: NativeStorage) : KeyValueStorage {
  override fun read(key: String): String? = storage.getOrNull(prefixed(key))
  override fun write(key: String, value: String) {
    storage[prefixed(key)] = value
  }

  private fun prefixed(key: String) = "flappy.$key"
}
