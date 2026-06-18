#!/bin/bash
# SuperGit-Jevi 릴리스 빌드 스크립트

set -e

echo "========================================"
echo "SuperGit-Jevi Release Build"
echo "========================================"
echo

# 버전 확인
VERSION=$(grep -oP '(?<=<version>)[^<]+' pom.xml | head -1)
echo "Version: $VERSION"
echo

# 클린 빌드
echo "[1/4] Cleaning..."
mvn clean

# 패키징
echo "[2/4] Building..."
mvn package -DskipTests

# 릴리스 디렉토리 생성
RELEASE_DIR="release/supergit-jevi-$VERSION"
echo "[3/4] Creating release directory: $RELEASE_DIR"
mkdir -p "$RELEASE_DIR"

# 파일 복사
cp target/supergit-jevi.jar "$RELEASE_DIR/"
cp jevi.sh "$RELEASE_DIR/"
cp jevi.bat "$RELEASE_DIR/"
cp README.md "$RELEASE_DIR/"
cp INSTALL.md "$RELEASE_DIR/"
cp .gitignore "$RELEASE_DIR/" 2>/dev/null || true

# 실행 권한 부여
chmod +x "$RELEASE_DIR/jevi.sh"

# 압축
echo "[4/4] Creating archives..."
cd release

# Linux/macOS용 tar.gz
tar -czf "supergit-jevi-$VERSION-unix.tar.gz" "supergit-jevi-$VERSION"
echo "Created: supergit-jevi-$VERSION-unix.tar.gz"

# Windows용 zip
zip -r "supergit-jevi-$VERSION-windows.zip" "supergit-jevi-$VERSION" > /dev/null
echo "Created: supergit-jevi-$VERSION-windows.zip"

cd ..

echo
echo "========================================"
echo "Build Complete!"
echo "========================================"
echo "Release files created in: release/"
echo
echo "Unix:    release/supergit-jevi-$VERSION-unix.tar.gz"
echo "Windows: release/supergit-jevi-$VERSION-windows.zip"
echo
echo "To test:"
echo "  cd release/supergit-jevi-$VERSION"
echo "  ./jevi.sh      (Linux/macOS)"
echo "  jevi.bat       (Windows)"
