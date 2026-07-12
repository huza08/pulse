#!/usr/bin/env bash
# Downloads static yt-dlp + ffmpeg binaries for bundling with Pulse Desktop.
# Supports Linux, macOS (arm64 + x86_64), and Windows.
# Places binaries in OS-specific subdirs: native/{linux,macos,windows}/
#
# Usage: bash download-binaries.sh
set -euo pipefail

detect_os() {
  case "$(uname -s)" in
    Linux*)  echo "linux" ;;
    Darwin*) echo "macos" ;;
    MINGW*|MSYS*|CYGWIN*) echo "windows" ;;
    *)       echo "unknown" ;;
  esac
}

detect_arch() {
  case "$(uname -m)" in
    x86_64|amd64) echo "x86_64" ;;
    arm64|aarch64) echo "arm64" ;;
    *)            echo "unknown" ;;
  esac
}

OS=$(detect_os)
ARCH=$(detect_arch)

if [ "$OS" = "unknown" ]; then
  echo "ERROR: Unsupported OS. Only Linux, macOS, and Windows are supported."
  exit 1
fi

# place in OS-specific subdir
NATIVE_DIR="src/main/resources/native/$OS"
mkdir -p "$NATIVE_DIR"

echo "==> Detected: $OS ($ARCH)"
echo ""

download_linux() {
  echo "==> Downloading yt-dlp (Linux x86_64)..."
  curl -fsSL "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_linux" \
    -o "$NATIVE_DIR/yt-dlp"
  chmod +x "$NATIVE_DIR/yt-dlp"
  echo "    OK: $(du -h "$NATIVE_DIR/yt-dlp" | cut -f1)"

  echo "==> Downloading ffmpeg (Linux x86_64)..."
  local FFMPEG_TAR="/tmp/ffmpeg-static.tar.xz"
  curl -fsSL \
    "https://github.com/BtbN/FFmpeg-Builds/releases/latest/download/ffmpeg-n7.1-latest-linux64-gpl-7.1.tar.xz" \
    -o "$FFMPEG_TAR"
  tar -xf "$FFMPEG_TAR" -C /tmp/
  cp "/tmp/ffmpeg-n7.1-latest-linux64-gpl-7.1/bin/ffmpeg" "$NATIVE_DIR/ffmpeg"
  chmod +x "$NATIVE_DIR/ffmpeg"
  rm -rf "$FFMPEG_TAR" "/tmp/ffmpeg-n7.1-latest-linux64-gpl-7.1"
  echo "    OK: $(du -h "$NATIVE_DIR/ffmpeg" | cut -f1)"
}

download_macos() {
  local YTDLP_NAME="yt-dlp_macos"
  [ "$ARCH" = "arm64" ] && YTDLP_NAME="yt-dlp_macos-arm64"

  echo "==> Downloading yt-dlp (macOS $ARCH)..."
  curl -fsSL "https://github.com/yt-dlp/yt-dlp/releases/latest/download/$YTDLP_NAME" \
    -o "$NATIVE_DIR/yt-dlp"
  chmod +x "$NATIVE_DIR/yt-dlp"
  echo "    OK: $(du -h "$NATIVE_DIR/yt-dlp" | cut -f1)"

  # macOS: use static builds from well-known sources
  # not tested yet
  # Intel: evermeet.cx
  # ARM: osxexperts.net
  echo "==> Downloading ffmpeg (macOS $ARCH)..."
  local FFMPEG_FILE=$(mktemp)
  if [ "$ARCH" = "arm64" ]; then
    # arm
    curl -fsSL "https://www.osxexperts.net/ffmpeg8arm64.zip" -o "$FFMPEG_FILE"
    unzip -o "$FFMPEG_FILE" -d "/tmp/ffmpeg-macos/" >/dev/null 2>&1
    cp "/tmp/ffmpeg-macos/ffmpeg" "$NATIVE_DIR/ffmpeg"
    rm -rf "/tmp/ffmpeg-macos"
  else
    # intel
    curl -fsSL "https://evermeet.cx/ffmpeg/get/zip" -o "$FFMPEG_FILE"
    unzip -o "$FFMPEG_FILE" -d "/tmp/ffmpeg-macos/" >/dev/null 2>&1
    cp "/tmp/ffmpeg-macos/ffmpeg" "$NATIVE_DIR/ffmpeg"
    rm -rf "/tmp/ffmpeg-macos"
  fi

  chmod +x "$NATIVE_DIR/ffmpeg"
  rm -f "$FFMPEG_FILE"
  echo "    OK: $(du -h "$NATIVE_DIR/ffmpeg" | cut -f1)"
}

download_windows() {
  echo "==> Downloading yt-dlp.exe (Windows)..."
  curl -fsSL "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe" \
    -o "$NATIVE_DIR/yt-dlp.exe"
  echo "    OK: $(du -h "$NATIVE_DIR/yt-dlp.exe" | cut -f1)"

  echo "==> Downloading ffmpeg.exe (Windows)..."
  local FFMPEG_ZIP="/tmp/ffmpeg-static.zip"
  curl -fsSL \
    "https://github.com/BtbN/FFmpeg-Builds/releases/latest/download/ffmpeg-n7.1-latest-win64-gpl-7.1.zip" \
    -o "$FFMPEG_ZIP"
  unzip -o "$FFMPEG_ZIP" -d "/tmp/" >/dev/null 2>&1
  cp "/tmp/ffmpeg-n7.1-latest-win64-gpl-7.1/bin/ffmpeg.exe" "$NATIVE_DIR/ffmpeg.exe"
  rm -rf "$FFMPEG_ZIP" "/tmp/ffmpeg-n7.1-latest-win64-gpl-7.1"
  echo "    OK: $(du -h "$NATIVE_DIR/ffmpeg.exe" | cut -f1)"
}

case "$OS" in
  linux)   download_linux ;;
  macos)   download_macos ;;
  windows) download_windows ;;
esac

echo ""
echo "==> Done. Binaries in $NATIVE_DIR/"
ls -lh "$NATIVE_DIR/"
