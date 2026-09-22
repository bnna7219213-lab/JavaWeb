#!/usr/bin/env bash
#
# Advanced GraalVM Native Image Build Script
# Supports: Local GraalVM, Docker Multi-stage, Paketo Buildpacks
#
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_DIR"

APP_NAME="graalvm-native-advanced"
APP_VERSION="1.0.0"
MAIN_CLASS="com.example.graalvm.GraalVmAdvancedApplication"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info()  { echo -e "${BLUE}[INFO]${NC} $*"; }
log_ok()    { echo -e "${GREEN}[OK]${NC} $*"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC} $*"; }
log_error() { echo -e "${RED}[ERROR]${NC} $*"; }

usage() {
    cat <<EOF
Usage: build.sh [OPTIONS]

Build GraalVM Native Image for the Advanced Demo project.

Options:
  --mode MODE     Build mode: local, docker, paketo (default: local)
  --arch ARCH     Target architecture: amd64, arm64 (default: host)
  --skip-tests    Skip unit tests
  --help          Show this help message

Examples:
  ./build.sh                          # Local GraalVM build
  ./build.sh --mode docker            # Docker multi-stage build
  ./build.sh --mode paketo            # Paketo Buildpacks
  ./build.sh --mode docker --arch arm64  # Docker ARM64 build
EOF
}

MODE="local"
ARCH=""
SKIP_TESTS=""

while [[ $# -gt 0 ]]; do
    case $1 in
        --mode) MODE="$2"; shift 2;;
        --arch) ARCH="$2"; shift 2;;
        --skip-tests) SKIP_TESTS="-DskipTests"; shift;;
        --help) usage; exit 0;;
        *) log_error "Unknown option: $1"; usage; exit 1;;
    esac
done

check_prerequisites() {
    log_info "Checking prerequisites..."
    
    if ! command -v mvn &> /dev/null; then
        log_error "Maven not found. Install from https://maven.apache.org"
        exit 1
    fi
    
    if [[ "$MODE" == "local" ]]; then
        if ! command -v native-image &> /dev/null; then
            log_error "native-image not found. Install with: gu install native-image"
            exit 1
        fi
        log_ok "native-image: $(native-image --version | head -1)"
    fi
    
    if [[ "$MODE" == "docker" ]] || [[ "$MODE" == "paketo" ]]; then
        if ! command -v docker &> /dev/null; then
            log_error "Docker not found for $MODE build mode"
            exit 1
        fi
    fi
    
    log_ok "Maven: $(mvn -version | head -1)"
    log_ok "Java: $(java -version 2>&1 | head -1)"
    echo ""
}

build_local() {
    log_info "Building with local GraalVM..."
    
    # Step 1: AOT Processing
    log_info "[1/4] Running AOT processing..."
    mvn spring-boot:process-aot -q
    log_ok "AOT processing complete"
    
    # Step 2: Package
    log_info "[2/4] Packaging application..."
    local mvn_args="-DskipTests"
    mvn clean package $mvn_args -q
    log_ok "Package ready: target/${APP_NAME}-${APP_VERSION}.jar"
    
    # Step 3: Native Compilation
    log_info "[3/4] Compiling native image (3-5 min)..."
    local ni_args=(
        "--no-fallback"
        "-H:+ReportExceptionStackTraces"
        "-H:Name=${APP_NAME}"
        "-O2"
        "-march=compatibility"
    )
    
    if [[ -n "$ARCH" ]]; then
        ni_args+=("--platform=linux/${ARCH}")
    fi
    
    native-image "${ni_args[@]}" -jar "target/${APP_NAME}-${APP_VERSION}.jar"
    log_ok "Native image: target/${APP_NAME}"
    
    # Step 4: Verify
    log_info "[4/4] Verifying..."
    chmod +x "target/${APP_NAME}"
    local size=$(du -h "target/${APP_NAME}" | cut -f1)
    log_ok "Executable size: $size"
}

build_docker() {
    log_info "Building with Docker multi-stage..."
    
    local image_name="${APP_NAME}:latest"
    local dockerfile="ci/Dockerfile"
    
    local build_args=(
        "--build-arg" "APP_NAME=${APP_NAME}"
        "--build-arg" "APP_VERSION=${APP_VERSION}"
        "--build-arg" "MAIN_CLASS=${MAIN_CLASS}"
    )
    
    if [[ -n "$ARCH" ]]; then
        build_args+=("--platform" "linux/${ARCH}")
    fi
    
    docker build "${build_args[@]}" -f "$dockerfile" -t "$image_name" .
    
    log_ok "Docker image: $image_name"
    log_info "Run: docker run -p 8104:8104 $image_name"
}

build_paketo() {
    log_info "Building with Paketo Buildpacks..."
    
    mvn spring-boot:build-image \
        -Dspring-boot.build-image.imageName="${APP_NAME}:latest" \
        -Dspring-boot.build-image.env.BP_NATIVE_IMAGE=true \
        -Dspring-boot.build-image.env.BP_JVM_VERSION=21 \
        -DskipTests
    
    log_ok "Paketo image: ${APP_NAME}:latest"
    log_info "Run: docker run -p 8104:8104 ${APP_NAME}:latest"
}

smoke_test() {
    local port=$1
    
    log_info "Running smoke test on port $port..."
    sleep 2
    
    local health
    health=$(curl -s http://localhost:${port}/api/perf/health || echo '{"status":"FAIL"}')
    
    if echo "$health" | grep -q '"status":"UP"'; then
        log_ok "Health check PASSED"
        echo "  Response: $health"
    else
        log_warn "Health check response: $health"
    fi
}

print_summary() {
    echo ""
    echo "===== BUILD SUMMARY ====="
    echo "  Mode:   $MODE"
    echo "  App:    $APP_NAME v$APP_VERSION"
    echo "  Port:   8104"
    echo "  APIs:"
    echo "    /               Performance panel"
    echo "    /compare        Comparison view"
    echo "    /api/perf/*     Metrics APIs"
    echo "=========================="
}

# Main
check_prerequisites

case $MODE in
    local)   build_local ;;
    docker)  build_docker ;;
    paketo)  build_paketo ;;
    *)       log_error "Unknown mode: $MODE"; usage; exit 1 ;;
esac

print_summary
