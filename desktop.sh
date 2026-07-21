#!/usr/bin/env bash
set -euo pipefail

# desktop build script
# usage: ./build-desktop.sh [run|package]
#   run     - build and run (default)
#   package - build distributable package

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

export JAVA_HOME=/usr/lib/jvm/java-26-openjdk // ts my setup vro
export ANDROID_HOME=/home/huza/sdk // configure ur shi here

ACTION="${1:-run}"

case "$ACTION" in
run)
    echo "build then run desktop app..."
    ./gradlew :desktop:run
    ;;
package)
    echo "building distributable package..."
    ./gradlew :desktop:packageDistributionForCurrentOS
    echo "Package at: $SCRIPT_DIR/desktop/build/compose/binaries/"
    ;;
*)
    echo "usage: $0 [run|package]"
    exit 1
    ;;
esac
