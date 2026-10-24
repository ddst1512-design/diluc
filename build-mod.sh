#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
if ! command -v java >/dev/null 2>&1; then
  echo 'Install a 64-bit JDK 21 first.' >&2
  exit 1
fi
sh ./gradlew --no-daemon build
printf '\nBuilt: build/libs/diluc-armor-1.0.0.jar\n'
