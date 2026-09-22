@echo off
REM Advanced GraalVM Native Image Build Script (Windows)
REM Supports: Local GraalVM build

setlocal enabledelayedexpansion

set "APP_NAME=graalvm-native-advanced"
set "APP_VERSION=1.0.0"
set "MAIN_CLASS=com.example.graalvm.GraalVmAdvancedApplication"

echo ============================================
echo   GraalVM Native Image Build - Advanced
echo ============================================
echo.

REM Check prerequisites
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Java not found. Install GraalVM JDK 21.
    exit /b 1
)

where native-image >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] native-image not found. Run: gu install native-image
    exit /b 1
)

where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Maven not found.
    exit /b 1
)

echo [OK] Prerequisites verified.
echo.

REM Step 1: AOT processing
echo [1/4] Running AOT processing...
call mvn spring-boot:process-aot -q
echo [OK] AOT processing complete.
echo.

REM Step 2: Package
echo [2/4] Packaging application...
call mvn clean package -DskipTests -q
echo [OK] Package ready: target\%APP_NAME%-%APP_VERSION%.jar
echo.

REM Step 3: Native compilation
echo [3/4] Compiling native image (3-5 min)...
call native-image ^
    --no-fallback ^
    -H:+ReportExceptionStackTraces ^
    -H:Name="%APP_NAME%" ^
    -O2 ^
    -march=compatibility ^
    -jar "target\%APP_NAME%-%APP_VERSION%.jar"

if %errorlevel% neq 0 (
    echo [ERROR] Native image compilation failed.
    exit /b 1
)

echo [OK] Native image: target\%APP_NAME%.exe
echo.

REM Step 4: Summary
echo ============================================
echo   BUILD COMPLETE
echo ============================================
echo   Executable: target\%APP_NAME%.exe
echo   Run: target\%APP_NAME%.exe
echo   URL: http://localhost:8104/
echo ============================================

endlocal
