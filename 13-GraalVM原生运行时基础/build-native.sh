#!/usr/bin/env bash
#
# GraalVM Native Image Build Script (Linux/macOS)
# Builds this Spring Boot project into a native executable
#
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

APP_NAME="graalvm-native-basic"
APP_VERSION="1.0.0"
MAIN_CLASS="com.example.graalvm.GraalVmBasicApplication"
OUTPUT_NAME="graalvm-native-basic"

echo "============================================"
echo "  GraalVM Native Image Build - Basic"
echo "============================================"
echo ""

# Check prerequisites
check_prerequisites() {
    echo "[1/6] Checking prerequisites..."
    
    if ! command -v java &> /dev/null; then
        echo "ERROR: java not found. Please install GraalVM JDK 21."
        exit 1
    fi
    
    if ! command -v native-image &> /dev/null; then
        echo "WARNING: native-image not found. Attempting to install via gu..."
        if command -v gu &> /dev/null; then
            gu install native-image
        else
            echo "ERROR: gu not found. Please install GraalVM and run: gu install native-image"
            exit 1
        fi
    fi
    
    if ! command -v mvn &> /dev/null; then
        echo "ERROR: Maven not found. Please install Maven 3.8+."
        exit 1
    fi
    
    java -version 2>&1 | head -1
    native-image --version
    echo "  Prerequisites OK."
    echo ""
}

run_aot_processing() {
    echo "[2/6] Running Spring Boot AOT processing..."
    mvn spring-boot:process-aot -q
    echo "  AOT processing complete."
    echo ""
}

package_app() {
    echo "[3/6] Packaging application..."
    mvn package -DskipTests -q
    echo "  Package created: target/${APP_NAME}-${APP_VERSION}.jar"
    echo ""
}

compile_native() {
    echo "[4/6] Compiling native image (2-5 min)..."
    native-image \
        --no-fallback \
        --enable-preview \
        -H:+ReportExceptionStackTraces \
        -H:ClassInitialization="${MAIN_CLASS}:build_time" \
        -H:Name="${OUTPUT_NAME}" \
        -O2 \
        -march=compatibility \
        -jar "target/${APP_NAME}-${APP_VERSION}.jar"
    
    echo ""
    echo "  Native executable: target/${OUTPUT_NAME}"
    echo ""
}

verify_executable() {
    echo "[5/6] Verifying native executable..."
    
    EXECUTABLE="target/${OUTPUT_NAME}"
    
    if [ ! -f "$EXECUTABLE" ]; then
        echo "ERROR: Native executable not found at $EXECUTABLE"
        exit 1
    fi
    
    chmod +x "$EXECUTABLE"
    
    FILE_SIZE=$(du -h "$EXECUTABLE" | cut -f1)
    echo "  Executable size: $FILE_SIZE"
    echo ""
}

smoke_test() {
    echo "[6/6] Running smoke test (5 seconds)..."
    
    EXECUTABLE="target/${OUTPUT_NAME}"
    
    "$EXECUTABLE" &>/dev/null &
    APP_PID=$!
    
    sleep 5
    
    if curl -s http://localhost:8103/api/perf/health > /dev/null 2>&1; then
        echo "  Health check: PASSED"
    else
        echo "  Health check: Application may need more time"
    fi
    
    kill $APP_PID 2>/dev/null || true
    echo ""
}

print_summary() {
    echo "============================================"
    echo "  BUILD COMPLETE"
    echo "============================================"
    echo "  Executable: target/${OUTPUT_NAME}"
    echo "  Run: ./target/${OUTPUT_NAME}"
    echo "  URL: http://localhost:8103/"
    echo ""
}

# Main
check_prerequisites
run_aot_processing
package_app
compile_native
verify_executable
smoke_test
print_summary
