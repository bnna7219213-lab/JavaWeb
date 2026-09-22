# GraalVM Installation and Native Image Compilation Guide

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Installing GraalVM](#installing-graalvm)
3. [Installing native-image Tool](#installing-native-image-tool)
4. [Project-Specific AOT Configuration](#project-specific-aot-configuration)
5. [Compiling to Native Image](#compiling-to-native-image)
6. [Runtime Verification](#runtime-verification)
7. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### System Requirements

| Requirement    | Specification                          |
|----------------|----------------------------------------|
| OS             | Linux / macOS / Windows 10+            |
| JDK            | GraalVM JDK 21 (included in GraalVM)   |
| RAM            | 4GB+ recommended (compilation uses ~2GB)|
| Disk Space     | ~2GB for GraalVM installation          |
| Maven          | 3.8+                                   |
| Docker (optional) | For containerized builds            |

### Check Environment

```bash
# Verify JDK 21 is available
java -version
# Expected: java version "21.0.x" ...

# Verify Maven is available
mvn -version
# Expected: Apache Maven 3.8+
```

---

## Installing GraalVM

### Method 1: Manual Installation (Recommended for Full Control)

#### Linux/macOS

```bash
# Download GraalVM CE 21
# Visit: https://github.com/graalvm/graalvm-ce-builds/releases
wget https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-21.0.2/graalvm-community-jdk-21.0.2_linux-x64_bin.tar.gz

# Extract
tar -xzf graalvm-community-jdk-21.0.2_linux-x64_bin.tar.gz -C /opt/

# Set environment variables
export GRAALVM_HOME=/opt/graalvm-community-openjdk-21.0.2+13.1
export JAVA_HOME=$GRAALVM_HOME
export PATH=$GRAALVM_HOME/bin:$PATH

# Verify
java -version
# Should show: openjdk version "21.0.2" ... GraalVM
```

#### Windows

```powershell
# Download from: https://github.com/graalvm/graalvm-ce-builds/releases
# graalvm-community-jdk-21.0.2_windows-x64_bin.zip

# Extract to C:\graalvm
# Set environment variables
$env:GRAALVM_HOME = "C:\graalvm\graalvm-community-openjdk-21.0.2+13.1"
$env:JAVA_HOME = $env:GRAALVM_HOME
$env:PATH = "$env:GRAALVM_HOME\bin;$env:PATH"

# Verify in new terminal
java -version
```

### Method 2: SDKMAN (Linux/macOS)

```bash
# Install SDKMAN if not available
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# List available GraalVM versions
sdk list java | grep graal

# Install GraalVM CE 21
sdk install java 21.0.2-graalce

# Set as default
sdk use java 21.0.2-graalce

# Verify
java -version
```

### Method 3: Homebrew (macOS)

```bash
# Install GraalVM
brew install --cask graalvm-jdk@21

# Set JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Verify
java -version
```

### Method 4: Chocolatey (Windows)

```powershell
choco install graalvm-jdk --version=21.0.2

# Verify
java -version
```

---

## Installing native-image Tool

### Using GraalVM Updater (gu)

```bash
# Install native-image component
gu install native-image

# Verify installation
native-image --version
# Expected: GraalVM 21.0.2 ... native-image 21.0.2
```

### Verify Full Installation

```bash
java -version
native-image --version
gu list

# Check that polyglot support is available
java -polyglot --version
```

---

## Project-Specific AOT Configuration

### Step 1: Configure pom.xml

The project pom.xml includes:

1. **spring-boot-maven-plugin** with `<image>` configuration for Paketo buildpacks
2. **spring-boot-aot-maven-plugin** for AOT asset generation
3. **native-maven-plugin** for direct GraalVM compilation

### Step 2: Register RuntimeHints

All entities and resources used via dynamic patterns need hints:

```java
// BasicRuntimeHintsRegistrar.java
@Override
public void registerHints(RuntimeHints hints, ClassLoader cl) {
    // Reflection for JSON serialization
    hints.reflection().registerType(User.class, MemberCategory.values());

    // Resource patterns for templates
    hints.resources().registerPattern("templates/**");

    // Serialization support
    hints.serialization().registerType(User.class);
}
```

### Step 3: AOT Configuration Class

```java
@Configuration
@ImportRuntimeHints(BasicRuntimeHintsRegistrar.class)
public class AotConfig { }
```

---

## Compiling to Native Image

### Method 1: Spring Boot Buildpacks (Recommended)

Uses Paketo Buildpacks to compile inside Docker. No local GraalVM required.

```bash
# Build docker image with native executable
mvn spring-boot:build-image

# Run the native docker image
docker run -p 8103:8103 graalvm-native-basic:latest

# For GraalVM CE builds, use the bp-native-image env:
mvn spring-boot:build-image -Dspring-boot.build-image.env.BP_NATIVE_IMAGE=true
```

### Method 2: GraalVM Maven Plugin (Direct Compilation)

Requires GraalVM installed locally.

```bash
# Run AOT processing + native compilation
mvn -Pnative -DskipTests clean package native:compile

# Or step by step:
# Step 1: AOT processing
mvn spring-boot:process-aot

# Step 2: Package
mvn package -DskipTests

# Step 3: Native compile
mvn native:compile -DskipTests
```

### Method 3: Direct native-image Command

```bash
# Package as fat JAR
mvn package -DskipTests

# Compile with native-image
native-image \
    --no-fallback \
    --enable-preview \
    -H:+ReportExceptionStackTraces \
    -H:Class=com.example.graalvm.GraalVmBasicApplication \
    -O2 \
    -jar target/graalvm-native-basic-1.0.0.jar \
    graalvm-native-basic

# Run the native executable
./target/graalvm-native-basic
```

### Method 4: Docker-based Build (CI/CD)

```bash
# Use GraalVM Docker image for compilation
docker run --rm \
    -v $(pwd):/project \
    -w /project \
    ghcr.io/graalvm/graalvm-community:21 \
    bash -c "mvn package -DskipTests && \
             native-image --no-fallback \
             -jar target/graalvm-native-basic-1.0.0.jar"
```

---

## Runtime Verification

### Check if Running as Native Image

```bash
# Health endpoint should report native mode
curl http://localhost:8103/api/perf/health
# Native response: {"status":"UP","mode":"native"}
# JVM response:   {"status":"UP","mode":"jvm"}

# Check startup time from headers or /
curl -s http://localhost:8103/api/perf/metrics | python -m json.tool
```

### Expected Performance

| Metric               | Native Image | JVM Dev Mode |
|----------------------|-------------|--------------|
| Startup Time         | 50-150ms    | 2000-5000ms  |
| RSS Memory           | 40-80MB     | 200-400MB    |
| Binary Size          | 60-120MB    | N/A          |
| Peak Throughput      | Higher      | Comparable   |

---

## Troubleshooting

### Common Issues

#### 1. "Class not found" at runtime

**Cause**: Missing RuntimeHints registration.

**Fix**: Ensure all classes used via reflection are registered:

```java
hints.reflection().registerType(MyClass.class, MemberCategory.values());
```

#### 2. "Resource not found" at runtime

**Cause**: Template or resource not registered.

**Fix**: Add resource pattern:

```java
hints.resources().registerPattern("templates/**");
```

#### 3. Compilation OutOfMemory

**Cause**: Default heap too small for compilation.

**Fix**: Increase compiler memory:

```bash
export NATIVE_IMAGE_OPTIONS="-Xmx4g"
native-image -Xmx4g --no-fallback -jar target/app.jar
```

#### 4. "No matching resource pattern found"

**Cause**: Files in `src/main/resources` not registered for classpath access.

```java
hints.resources().registerPattern("*.properties");
hints.resources().registerPattern("*.yml");
```

#### 5. Windows Long Path Issue

**Fix**: Enable long paths in Windows Registry or shorten project path.

#### 6. Fallback Image Warning

The `--no-fallback` flag ensures pure native compilation. If removed, GraalVM creates a JVM fallback binary (much larger, slower).

### Debug Compilation

```bash
# Verbose output
native-image -v --no-fallback -jar target/app.jar

# Generate compilation report
native-image --no-fallback -H:+PrintAnalysisCallTree -jar target/app.jar

# Include debug symbols
native-image --no-fallback -g -jar target/app.jar
```
