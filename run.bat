@echo off
REM ==============================================================================
REM SyncSphere — Team Contribution & Task Management System
REM Build and Run Script for Windows Command Prompt
REM ==============================================================================

echo ==============================================================================
echo                 SyncSphere Desktop Application Launcher                      
echo ==============================================================================

REM Check if javac and java are available
where javac >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java Development Kit (JDK) 17+ is required but 'javac' was not found in PATH.
    echo Please install JDK 17 or higher and ensure it is added to your environment variables.
    pause
    exit /b 1
)

REM Create out directory if missing
if not exist "out" mkdir out

echo [1/2] Compiling SyncSphere Java Sources...
javac -encoding UTF-8 -cp "lib/*" -d out src/contracts/*.java src/exception/*.java src/model/*.java src/repository/*.java src/service/*.java src/ui/*.java src/app/*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed! Please review the error messages above.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Launching SyncSphere Application...
java -cp "out;lib/*" app.SyncSphereApp

if %ERRORLEVEL% NEQ 0 (
    echo [NOTE] Application exited with code %ERRORLEVEL%.
)

pause
