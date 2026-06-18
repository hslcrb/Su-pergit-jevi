# SuperGit-Jevi 설치 가이드

## 시스템 요구사항

- **Java 17 이상** (필수)
- Git 설치 권장 (JGit 내장이지만 Git CLI도 유용)
- 지원 OS: Windows, Linux, macOS

## Java 설치 확인

```bash
java -version
```

Java 17 이상이 설치되어 있어야 합니다.

### Java 설치 방법

**Windows:**
1. [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) 다운로드
2. 또는 [OpenJDK](https://adoptium.net/) 다운로드
3. 설치 후 환경변수 설정 (자동)

**Linux:**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# Fedora/RHEL
sudo dnf install java-17-openjdk

# Arch
sudo pacman -S jdk-openjdk
```

**macOS:**
```bash
# Homebrew 사용
brew install openjdk@17
```

## 설치 방법

### 방법 1: 소스에서 빌드 (권장)

```bash
# 저장소 클론
git clone https://github.com/yourusername/supergit-jevi.git
cd supergit-jevi

# Maven으로 빌드
mvn clean package

# 실행 확인
java -jar target/supergit-jevi.jar
```

### 방법 2: 릴리스 다운로드

1. [Releases 페이지](https://github.com/yourusername/supergit-jevi/releases)에서 최신 버전 다운로드
2. `supergit-jevi.jar` 파일을 원하는 위치에 저장
3. 아래 "사용 방법" 참고

## 사용 방법

### Windows

#### 방법 A: 배치 파일 사용 (추천)

1. `jevi.bat` 파일을 SuperGit-Jevi 폴더에 저장
2. 이 폴더를 PATH에 추가:
   - `시스템 속성` > `환경 변수` > `Path` 편집
   - SuperGit-Jevi 폴더 경로 추가
3. 아무 Git 저장소에서:
   ```cmd
   jevi
   ```

#### 방법 B: 직접 실행

```cmd
java -jar C:\path\to\supergit-jevi.jar
```

#### 방법 C: 별칭 설정 (PowerShell)

PowerShell 프로필 편집:
```powershell
notepad $PROFILE
```

다음 줄 추가:
```powershell
function jevi { java -jar "C:\path\to\supergit-jevi.jar" $args }
```

### Linux / macOS

#### 방법 A: 셸 스크립트 사용 (추천)

1. `jevi.sh`를 실행 가능하게 만들기:
   ```bash
   chmod +x jevi.sh
   ```

2. 심볼릭 링크 생성:
   ```bash
   sudo ln -s /path/to/supergit-jevi/jevi.sh /usr/local/bin/jevi
   ```

3. 아무 Git 저장소에서:
   ```bash
   jevi
   ```

#### 방법 B: 별칭 설정

`~/.bashrc` 또는 `~/.zshrc`에 추가:
```bash
alias jevi='java -jar /path/to/supergit-jevi.jar'
```

적용:
```bash
source ~/.bashrc  # 또는 source ~/.zshrc
```

#### 방법 C: 직접 실행

```bash
java -jar /path/to/supergit-jevi.jar
```

## 설치 확인

설치가 완료되면 아무 Git 저장소에서:

```bash
jevi
```

다음과 같은 메뉴가 나타나면 성공:

```
===============================================================
  SuperGit-Jevi
  제비처럼 빠른 Git 도구에 오신 것을 환영합니다!
===============================================================

---------------------------------------------------------------
주 메뉴 - 원하는 작업을 선택하세요:
---------------------------------------------------------------
  [1] 상태 확인 (Status)
  [2] 변경사항 커밋 (Commit)
  ...
```

## 문제 해결

### "java: command not found"
- Java가 설치되지 않았거나 PATH에 없습니다
- Java 설치 후 터미널/cmd 재시작

### "Error: Unable to access jarfile"
- JAR 파일 경로가 잘못되었습니다
- 절대 경로로 시도해보세요

### "UnsupportedClassVersionError"
- Java 버전이 낮습니다
- Java 17 이상을 설치하세요

### Git 저장소를 찾을 수 없습니다
- Git 저장소가 초기화되지 않은 폴더입니다
- `git init` 실행 또는 `[9] 저장소 초기화/복제` 메뉴 사용

## 업데이트

새 버전으로 업데이트:

```bash
cd supergit-jevi
git pull
mvn clean package
```

## 제거

단순히 SuperGit-Jevi 폴더를 삭제하면 됩니다.

별칭/심볼릭 링크도 제거:
- Linux/macOS: `sudo rm /usr/local/bin/jevi`
- Windows: PATH에서 제거
- 셸 설정 파일에서 alias 줄 제거

---

**문제가 발생하면 [Issues](https://github.com/yourusername/supergit-jevi/issues)에 보고해주세요!**
