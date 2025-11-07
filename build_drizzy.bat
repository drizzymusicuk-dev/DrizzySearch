@echo off
setlocal enabledelayedexpansion

echo =========================================
echo        Drizzy Search - Android Builder
echo =========================================
echo.


set "PROJECT_DIR=%~dp0"
set "TOOLS_DIR=%PROJECT_DIR%tools"
set "GRADLE_VERSION=8.5"
set "GRADLE_ZIP=gradle-%GRADLE_VERSION%-bin.zip"
set "GRADLE_URL=https://services.gradle.org/distributions/%GRADLE_ZIP%"
set "GRADLE_DIR=%TOOLS_DIR%\gradle-%GRADLE_VERSION%"
set "GRADLE_BIN=%GRADLE_DIR%\bin\gradle.bat"
set "LOCAL_PROPERTIES=%PROJECT_DIR%local.properties"

if not defined ANDROID_HOME (
    if exist "%LOCAL_PROPERTIES%" (
        for /f "tokens=2 delims==" %%A in ('findstr sdk.dir "%LOCAL_PROPERTIES%"') do (
            set "ANDROID_HOME=%%~A"
        )
    )
)

if not defined ANDROID_HOME (
    echo Android SDK not configured.
    echo.
    set /p SDKPATH=Enter your Android SDK path (e.g. C:\Users\jeani\AppData\Local\Android\Sdk): 
    echo sdk.dir=!SDKPATH:\=\\!> "%LOCAL_PROPERTIES%"
    set "ANDROID_HOME=!SDKPATH!"
    echo.
    echo SDK path saved to local.properties
)

if not exist "%ANDROID_HOME%" (
    echo Error: Android SDK folder not found at "%ANDROID_HOME%"
    echo Please check your path and try again.
    pause
    exit /b 1
)


if not exist "%TOOLS_DIR%" mkdir "%TOOLS_DIR%"

if not exist "%GRADLE_BIN%" (
    echo Downloading Gradle %GRADLE_VERSION%...
    powershell -Command "Invoke-WebRequest '%GRADLE_URL%' -OutFile '%TOOLS_DIR%\%GRADLE_ZIP%'"
    echo Extracting Gradle...
    powershell -Command "Expand-Archive '%TOOLS_DIR%\%GRADLE_ZIP%' '%TOOLS_DIR%' -Force"
)

echo.
echo Building DrizzyApp with Gradle %GRADLE_VERSION%...
cd /d "%PROJECT_DIR%"
call "%GRADLE_BIN%" assembleDebug


set "APK_PATH=%PROJECT_DIR%app\build\outputs\apk\debug\app-debug.apk"

echo.
if exist "%APK_PATH%" (
    move "%APK_PATH%" "%PROJECT_DIR%DrizzyApp-debug.apk" >nul
    echo ✅ Build complete DrizzyApp-debug.apk
) else (
    echo ❌ Build failed. Check Gradle output above.
)

echo.
pause
