@echo off
setlocal
cd /d "%~dp0"
where java >nul 2>&1
if errorlevel 1 (
  echo Java 21 is required. Install a 64-bit JDK 21 and reopen this script.
  pause
  exit /b 1
)
call gradlew.bat --no-daemon build
if errorlevel 1 (
  echo Build failed. Read the error above; this source project has not been compiled in the authoring workspace.
  pause
  exit /b 1
)
echo.
echo Built: build\libs\diluc-armor-1.0.0.jar
echo Install that JAR on a Minecraft 1.21.1 NeoForge 21.1.264 client and server.
pause
