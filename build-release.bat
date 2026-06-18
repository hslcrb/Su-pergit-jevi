@echo off
REM SuperGit-Jevi Windows 릴리스 빌드 스크립트

echo ========================================
echo SuperGit-Jevi Release Build (Windows)
echo ========================================
echo.

REM Maven으로 클린 빌드
echo [1/4] Cleaning...
call mvn clean

echo [2/4] Building...
call mvn package -DskipTests

REM 버전 추출 (간단하게)
set VERSION=1.0.0

REM 릴리스 디렉토리 생성
set RELEASE_DIR=release\supergit-jevi-%VERSION%
echo [3/4] Creating release directory: %RELEASE_DIR%
if not exist release mkdir release
if not exist %RELEASE_DIR% mkdir %RELEASE_DIR%

REM 파일 복사
copy target\supergit-jevi.jar %RELEASE_DIR%\
copy jevi.sh %RELEASE_DIR%\
copy jevi.bat %RELEASE_DIR%\
copy README.md %RELEASE_DIR%\
copy INSTALL.md %RELEASE_DIR%\

echo [4/4] Creating ZIP archive...
cd release
powershell Compress-Archive -Path supergit-jevi-%VERSION% -DestinationPath supergit-jevi-%VERSION%-windows.zip -Force
cd ..

echo.
echo ========================================
echo Build Complete!
echo ========================================
echo Release files created in: release\
echo.
echo Windows: release\supergit-jevi-%VERSION%-windows.zip
echo.
echo To test:
echo   cd release\supergit-jevi-%VERSION%
echo   jevi.bat
echo.
pause
