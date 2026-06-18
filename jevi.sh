#!/bin/bash
# SuperGit-Jevi Linux/macOS 실행 스크립트
# Java 17 이상 필요

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
java -jar "$SCRIPT_DIR/target/supergit-jevi.jar" "$@"
