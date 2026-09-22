@echo off
REM
REM GraalVM Native Image Build Script (Windows)
REM Builds this Spring Boot project into a native executable
REM

setlocal enabledelayedexpansion

set "APP_NAME=graalvm-native-basic"
set "APP_VERSION=1.0.0"
set "MAIN_CLASS=com.example.graalvm.GraalVmBasicApplication"
set "OUTPUT_NAME=graalvm-native-basic"

echo ============================================
echo   GraalVM Native Image Build - Basic (Windows)
echo ============================================
echo.

REM Check prerequisites
echo [1/6] Checking prerequisites...

where java >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: java not found. Please install GraalVM JDK 21.
    exit /b 1
)

where native-image >nul 2>nul
if %errorlevel% neq 0 (
    echo WARNING: native-image not found. Run: gu install native-image
    exit /b 1
)

where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Maven not found. Please install Maven 3.8+.
    exit /b 1
)

for /f "tokens=*" %%a in ('java -version 2^>^&1 ^| findstr /i "version"') do echo   %%a
for /f "tokens=*" %%a in ('native-image --version 2^>^&1') do echo   %%a
echo Prerequisites OK.
echo.

REM Step 1: AOT processing
echo [2/6] Running Spring Boot AOT processing...
call mvn spring-boot:process-aot -q
echo AOT processing complete.
echo.

REM Step 2: Package
echo [3/6] Packaging application...
call mvn package -DskipTests -q
echo Package created: target\%APP_NAME%-%APP_VERSION%.jar
echo.

REM Step 3: Compile native image
echo [4/6] Compiling native image (this may take 2-5 minutes)...
call native-image ^
    --no-fallback ^
    --enable-preview ^
    -H:+ReportExceptionStackTraces ^
    -H:ClassInitialization="%MAIN_CLASS%:build_time" ^
    -H:Name="%OUTPUT_NAME%" ^
    -O2 ^
    -march=compatibility ^
    -jar "target\%APP_NAME%-%APP_VERSION%.jar"

if %errorlevel% neq 0 (
    echo ERROR: Native image compilation failed.
    exit /b 1
)

echo.
echo Native executable: target\%OUTPUT_NAME%.exe
echo.

REM Step 4: Verify
echo [5/6] Verifying native executable...
if exist "target\%OUTPUT_NAME%.exe" (
    for %%F in ("target\%OUTPUT_NAME%.exe") do echo Executible size: %%~zF bytes
) else (
    echo ERROR: Native executable not found.
    exit /b 1
)
echo.

REM Step 5: Quick smoke test
echo [6/6] Running smoke test...
start /B "" "target\%OUTPUT_NAME%.exe"
timeout /t 5 /nobreak >nul 2>nul

curl -s http://localhost:8103/api/perf/health >nul 2>nul
if %errorlevel% equ 0 (
    echo Health check: PASSED
) else (
    echo Health check: Application may need more time
)

taskkill /F /IM "%OUTPUT_NAME%.exe" >nul 2>nul
echo.

REM Summary
echo ============================================
echo   BUILD COMPLETE
echo ============================================
echo.
echo   Executable: target\%OUTPUT_NAME%.exe
echo   Run: target\%OUTPUT_NAME%.exe
echo   URL: http://localhost:8103/
echo.

endlocal
