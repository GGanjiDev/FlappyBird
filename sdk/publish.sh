#!/usr/bin/env bash
# Build game + publish thư viện com.appmb.flappy:flappybird (Android AAR + iOS klib) ra một Maven repository thư mục.
# Cách dùng:  sdk/publish.sh <thư mục repo> [version]
#   vd:       sdk/publish.sh ~/Documents/GitHub/KMP-SevenBalls/maven_local 1.0.0
# Cần JDK 21+ (build KorGE ở thư mục gốc yêu cầu 21+, build sdk chạy được với 17+).

# Dừng ngay khi có lệnh lỗi, biến chưa khai báo hoặc lỗi trong pipe
set -euo pipefail

# Thư mục repo đích (bắt buộc), đổi thành đường dẫn tuyệt đối
REPO_DIR="$(mkdir -p "${1:?Usage: sdk/publish.sh <maven repo dir> [version]}" && cd "$1" && pwd)"
# Version, mặc định 1.0.0
VERSION="${2:-1.0.0}"
# Thư mục chứa script này (sdk/)
SDK_DIR="$(cd "$(dirname "$0")" && pwd)"
# Repo tạm do Gradle publish ra (có cả file checksum)
STAGING_DIR="$SDK_DIR/flappybird/build/repo"

# 1. Build KorGE ở thư mục gốc: bản web 1 file build/single/flappybird.html
(cd "$SDK_DIR/.." && ./gradlew packSingleHtml)
# 2. Build thư viện và publish vào repo tạm (xoá bản cũ trước)
rm -rf "$STAGING_DIR"
(cd "$SDK_DIR" && ./gradlew :flappybird:publishAllPublicationsToFlappyRepository -Pflappy.repo="$STAGING_DIR" -Pflappy.version="$VERSION")
# 3. Thay thư viện trong repo đích: xoá các artifact flappybird cũ (kể cả target đã bỏ như desktop)...
rm -rf "$REPO_DIR"/com/appmb/flappy/flappybird*
# ... rồi copy sang, BỎ file checksum (.md5/.sha1/.sha256/.sha512): Gradle không cần chúng với repo thư mục
rsync -a --exclude '*.md5' --exclude '*.sha1' --exclude '*.sha256' --exclude '*.sha512' "$STAGING_DIR"/ "$REPO_DIR"/

# Báo kết quả
echo "Published com.appmb.flappy:flappybird:$VERSION to $REPO_DIR ($(find "$REPO_DIR/com/appmb/flappy" -type f | wc -l | tr -d ' ') files)"
