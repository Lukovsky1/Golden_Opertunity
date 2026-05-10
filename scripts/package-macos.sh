#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
APP_NAME="Golden Opportunity"
MAIN_CLASS="com.GoldenOpportunity.MainUIFrame"
JAR_NAME="Golden_Opertunity-1.0-SNAPSHOT-jar-with-dependencies.jar"
PACKAGE_TYPE="${1:-app-image}"
DEST_DIR="$ROOT_DIR/dist/macos"

cd "$ROOT_DIR"

mvn -DskipTests package
mkdir -p "$DEST_DIR"

jpackage \
  --type "$PACKAGE_TYPE" \
  --name "$APP_NAME" \
  --input target \
  --main-jar "$JAR_NAME" \
  --main-class "$MAIN_CLASS" \
  --dest "$DEST_DIR" \
  --java-options "-Dfile.encoding=UTF-8"
