# ==============================================================================
# SyncSphere — Team Contribution & Task Management System
# PowerShell Build and Execution Script
# ==============================================================================

Write-Host "==================================================================" -ForegroundColor Cyan
Write-Host "             SyncSphere Desktop Application Launcher              " -ForegroundColor Cyan
Write-Host "==================================================================" -ForegroundColor Cyan

# 1. Verify Java compiler
try {
    $javacVersion = javac -version 2>&1
    Write-Host "[OK] Detected: $javacVersion" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] 'javac' not found. Please install JDK 17+ and add to PATH." -ForegroundColor Red
    exit 1
}

# 2. Prepare output directory
if (!(Test-Path "out")) {
    New-Item -ItemType Directory -Path "out" | Out-Null
}

# 3. Compile sources
Write-Host "[1/2] Compiling SyncSphere Java Sources..." -ForegroundColor Yellow

$sources = @(
    "src/contracts/*.java",
    "src/exception/*.java",
    "src/model/*.java",
    "src/repository/*.java",
    "src/service/*.java",
    "src/ui/*.java",
    "src/app/*.java"
)

javac -encoding UTF-8 -cp "lib/*" -d out $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed with exit code $LASTEXITCODE." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[2/2] Launching SyncSphere Application..." -ForegroundColor Green
java -cp "out;lib/*" app.SyncSphereApp
