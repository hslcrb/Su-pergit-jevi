# SuperGit-Jevi

**제비처럼 빠르고 안전한 Git 터미널 도구**

SuperGit-Jevi는 Git을 더 쉽고 안전하게 사용할 수 있도록 설계된 Java 기반 TUI(Terminal User Interface) 도구입니다.

## 목차

- [주요 특징](#주요-특징)
- [시스템 요구사항](#시스템-요구사항)
- [설치 방법](#설치-방법)
  - [Windows](#windows-설치)
  - [Linux](#linux-설치)
  - [macOS](#macos-설치)
- [사용 방법](#사용-방법)
- [기능 목록](#기능-목록)
- [아키텍처](#아키텍처)
- [워크플로우](#워크플로우)
- [라이선스](#라이선스)

## 주요 특징

### 안전성

- **자동 충돌 방지**: Push 전 자동으로 Fetch와 Pull 수행
- **시스템 레벨 차단**: Pull 없이 Push하는 것을 원천 차단
- **자동 충돌 해결**: 3가지 전략(OURS, THEIRS, APPEND BOTH)으로 병합 충돌 자동 처리
- **안전한 Reset**: SOFT/HARD 모드 선택 가능, 경고 메시지 제공

### 사용성

- **대화형 메뉴**: 숫자 선택 방식의 직관적인 인터페이스
- **초보자 친화적**: 각 단계마다 상세한 설명 제공
- **컬러 터미널**: 상태별로 색상 구분하여 가독성 향상
- **플랫폼 독립적**: Windows, Linux, macOS 모두 지원

### 고급 기능

- **커밋 탐색기**: 과거 커밋을 상세히 탐색하고 시간 여행 가능
- **스마트 검색**: 커밋 메시지, 작성자, 코드 내용, 파일명, 날짜로 검색
- **스마트 비교**: 다양한 방식으로 변경사항 비교
- **스마트 임시 저장**: Stash를 쉽게 관리

### AI Agent 기능

- **Snapshot**: 저장소의 모든 히스토리와 현재 상태를 통합하여 AI가 이해 가능한 형식으로 제공
- **JSON/TEXT 형식**: AI 최적화 JSON 또는 사람이 읽기 쉬운 TEXT 형식 지원
- **파일 진화 추적**: 각 파일의 생성부터 현재까지 전체 변경 이력 추적
- **가상 파일 시스템**: 삭제된 파일 포함 완전한 파일 시스템 뷰 제공

## 시스템 요구사항

```mermaid
graph TD
    A[시스템 요구사항] --> B[Java 17 이상]
    A --> C[Git CLI 권장]
    A --> D[지원 OS]
    D --> E[Windows 10/11]
    D --> F[Linux 모든 배포판]
    D --> G[macOS 10.15+]
```

- **필수**: Java 17 이상
- **권장**: Git CLI 설치
- **지원 OS**: Windows, Linux, macOS

## 설치 방법

### 설치 프로세스 다이어그램

```mermaid
flowchart LR
    A[시작] --> B{Java 설치됨?}
    B -->|아니오| C[Java 17 설치]
    B -->|예| D[SuperGit-Jevi 다운로드]
    C --> D
    D --> E[빌드 또는 릴리스 사용]
    E --> F{OS 타입?}
    F -->|Windows| G[jevi.bat 사용]
    F -->|Linux/macOS| H[jevi.sh 사용]
    G --> I[완료]
    H --> I
```

### Java 설치 확인

모든 플랫폼에서 먼저 Java 버전을 확인하세요:

```bash
java -version
```

Java 17 이상이 표시되어야 합니다.

---

### Windows 설치

#### 1단계: Java 설치

**방법 A - Oracle JDK:**
1. https://www.oracle.com/java/technologies/downloads/ 방문
2. Windows 용 Java 17 또는 최신 버전 다운로드
3. 설치 파일 실행
4. 설치 완료 후 cmd에서 `java -version` 확인

**방법 B - OpenJDK (Adoptium):**
1. https://adoptium.net/ 방문
2. Windows x64 용 JDK 17 다운로드
3. MSI 파일 실행하여 설치
4. 환경변수 자동 설정됨

#### 2단계: SuperGit-Jevi 설치

**방법 1 - 릴리스 다운로드 (권장):**

```cmd
# 1. 릴리스 다운로드
# https://github.com/yourusername/supergit-jevi/releases

# 2. ZIP 압축 해제
# supergit-jevi-1.0.0-windows.zip

# 3. 폴더 이동
cd C:\supergit-jevi

# 4. 실행
jevi.bat
```

**방법 2 - 소스에서 빌드:**

```cmd
# 1. Git 저장소 클론
git clone https://github.com/yourusername/supergit-jevi.git
cd supergit-jevi

# 2. Maven 빌드
mvn clean package

# 3. 실행
jevi.bat
```

#### 3단계: PATH 설정 (선택사항)

어디서나 `jevi` 명령을 사용하려면:

1. `시스템 속성` 열기 (Win + Pause)
2. `고급 시스템 설정` 클릭
3. `환경 변수` 클릭
4. `시스템 변수`에서 `Path` 선택 후 `편집`
5. `새로 만들기` 클릭
6. SuperGit-Jevi 설치 경로 입력 (예: `C:\supergit-jevi`)
7. 확인 후 cmd 재시작
8. 이제 아무 곳에서나 `jevi` 실행 가능

---

### Linux 설치

#### 1단계: Java 설치

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
java -version
```

**Fedora/RHEL/CentOS:**
```bash
sudo dnf install java-17-openjdk java-17-openjdk-devel
java -version
```

**Arch Linux:**
```bash
sudo pacman -S jdk17-openjdk
java -version
```

#### 2단계: SuperGit-Jevi 설치

**방법 1 - 릴리스 다운로드:**

```bash
# 1. 릴리스 다운로드 및 압축 해제
wget https://github.com/yourusername/supergit-jevi/releases/download/v1.0.0/supergit-jevi-1.0.0-unix.tar.gz
tar -xzf supergit-jevi-1.0.0-unix.tar.gz
cd supergit-jevi-1.0.0

# 2. 실행
./jevi.sh
```

**방법 2 - 소스에서 빌드:**

```bash
# 1. 저장소 클론
git clone https://github.com/yourusername/supergit-jevi.git
cd supergit-jevi

# 2. 빌드
mvn clean package

# 3. 실행
./jevi.sh
```

#### 3단계: 시스템 전역 설치 (선택사항)

```bash
# 심볼릭 링크 생성
sudo ln -s $(pwd)/jevi.sh /usr/local/bin/jevi

# 이제 아무 디렉토리에서나 실행 가능
jevi
```

**또는 별칭 사용:**

```bash
# ~/.bashrc 또는 ~/.zshrc에 추가
echo 'alias jevi="java -jar /path/to/supergit-jevi/target/supergit-jevi.jar"' >> ~/.bashrc
source ~/.bashrc
```

---

### macOS 설치

#### 1단계: Java 설치

**Homebrew 사용 (권장):**
```bash
# Homebrew 설치 (없는 경우)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Java 17 설치
brew install openjdk@17

# PATH 설정
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# 확인
java -version
```

**수동 설치:**
1. https://adoptium.net/ 방문
2. macOS 용 JDK 17 다운로드
3. PKG 파일 실행하여 설치

#### 2단계: SuperGit-Jevi 설치

```bash
# 1. 저장소 클론
git clone https://github.com/yourusername/supergit-jevi.git
cd supergit-jevi

# 2. 빌드
mvn clean package

# 3. 실행
./jevi.sh
```

#### 3단계: 전역 설치 (선택사항)

```bash
# 심볼릭 링크 생성
sudo ln -s $(pwd)/jevi.sh /usr/local/bin/jevi

# 또는 별칭 사용 (zsh의 경우)
echo 'alias jevi="java -jar /path/to/supergit-jevi/target/supergit-jevi.jar"' >> ~/.zshrc
source ~/.zshrc
```

---

## 사용 방법

### 기본 실행

Git 저장소가 있는 디렉토리에서:

```bash
# Windows
jevi.bat

# Linux/macOS
./jevi.sh
# 또는 (전역 설치한 경우)
jevi
```

### 메뉴 구조

```
SuperGit-Jevi 메인 메뉴
├── [1] 상태 확인 (Status) (for human)
├── [2] 변경사항 커밋 (Commit) (for human)
├── [3] 원격 저장소로 Push (안전 모드) (for human)
├── [4] 브랜치 관리 (Branch) (for human)
├── [5] 커밋 히스토리 (History) (for human)
├── [6] 원격에서 Pull (for human)
├── === 고급 기능 ===
├── [7] 커밋 되돌리기 (Reset) (for human)
├── [8] 충돌 해결 (Conflict Resolver) (for human)
├── [9] 저장소 초기화/복제 (Init/Clone) (for human)
├── [10] 커밋 탐색기 (Explore) (for human)
├── [11] 스마트 검색 (Search) (for human)
├── [12] 스마트 비교 (Diff) (for human)
├── [13] 임시 저장 (Stash) (for human)
├── === AI Agent 기능 ===
├── [14] 스냅샷 (Snapshot) (for AI)
├── [h] 도움말 (for human, AI)
└── [0] 종료
```

## 기능 목록

### 기본 기능

```mermaid
graph TD
    A[기본 기능] --> B[Status]
    A --> C[Commit]
    A --> D[Push]
    A --> E[Pull]
    A --> F[Branch]
    A --> G[History]
    
    B --> B1[변경된 파일 목록]
    B --> B2[파일 상태 분류]
    
    C --> C1[자동 스테이징]
    C --> C2[커밋 메시지 입력]
    
    D --> D1[자동 Fetch]
    D --> D2[자동 Pull]
    D --> D3[충돌 검사]
    D --> D4[안전한 Push]
```

**1. Status (상태 확인)**
- 현재 브랜치 표시
- 수정/추가/삭제/추적안됨 파일 분류
- 충돌 파일 강조

**2. Commit (커밋)**
- 변경사항 자동 스테이징 옵션
- 커밋 메시지 입력
- 커밋 정보 표시

**3. Push (안전한 푸시)**
- 단계 1: 자동 Fetch
- 단계 2: 자동 Pull
- 단계 3: 충돌 검사
- 단계 4: Push 실행 (안전한 경우만)

**4. Pull (풀)**
- 원격 저장소 최신 변경사항 가져오기
- 병합 상태 표시

**5. Branch (브랜치 관리)**
- 브랜치 목록 보기
- 새 브랜치 생성
- 브랜치 전환
- 브랜치 삭제

**6. History (히스토리)**
- 커밋 목록 표시
- 해시, 메시지, 작성자, 날짜 정보

### 고급 기능

```mermaid
graph TD
    A[고급 기능] --> B[Reset]
    A --> C[Conflict Resolver]
    A --> D[Init/Clone]
    A --> E[Commit Explorer]
    A --> F[Smart Search]
    A --> G[Smart Diff]
    A --> H[Smart Stash]
    
    C --> C1[OURS 전략]
    C --> C2[THEIRS 전략]
    C --> C3[APPEND BOTH]
    
    E --> E1[파일 목록]
    E --> E2[Diff 보기]
    E --> E3[시간 여행]
    E --> E4[Cherry-pick]
    
    F --> F1[커밋 메시지 검색]
    F --> F2[작성자 검색]
    F --> F3[코드 검색]
    F --> F4[파일명 검색]
    F --> F5[날짜 검색]
```

**7. Reset (커밋 되돌리기)**
- SOFT 모드: 커밋만 취소, 변경사항 유지
- HARD 모드: 모든 것 삭제 (경고 포함)
- 1-10개 커밋 되돌리기

**8. Conflict Resolver (충돌 해결)**
- OURS: 로컬 변경사항 우선
- THEIRS: 원격 변경사항 우선
- APPEND BOTH: 양쪽 모두 병합

**9. Init/Clone (저장소 초기화/복제)**
- 새 저장소 초기화
- 원격 저장소 복제

**10. Commit Explorer (커밋 탐색기)**
- 커밋 상세 정보 표시
- 변경된 파일 목록
- 전체 Diff 보기
- 특정 파일 내용 보기
- 시간 여행 (과거 커밋으로 체크아웃)
- Cherry-pick

**11. Smart Search (스마트 검색)**
- 커밋 메시지에서 키워드 검색
- 작성자로 필터링
- 파일 내용에서 코드 검색 (줄 번호 표시)
- 파일명으로 검색
- 날짜 범위로 검색

**12. Smart Diff (스마트 비교)**
- 작업 디렉토리 vs HEAD
- 두 커밋 간 비교
- 브랜치 간 비교
- 특정 파일만 비교
- 통계 정보

**13. Smart Stash (스마트 임시 저장)**
- 현재 작업 임시 저장
- Stash 목록 보기
- Stash Pop (복원 후 삭제)
- Stash Apply (복원하되 유지)
- Stash Drop (삭제)

### AI Agent 전용 기능

**14. Snapshot (스냅샷) - AI를 위한 통합 뷰**

```mermaid
graph TD
    A[Snapshot] --> B[데이터 수집]
    B --> C[커밋 히스토리]
    B --> D[파일 시스템]
    B --> E[브랜치 구조]
    B --> F[통계 정보]
    
    C --> C1[모든 커밋]
    C --> C2[변경 파일]
    C --> C3[작성자 정보]
    
    D --> D1[현재 파일]
    D --> D2[파일 히스토리]
    D --> D3[삭제된 파일]
    
    A --> G[출력 형식]
    G --> H[JSON for AI]
    G --> I[TEXT for Human]
```

**주요 기능:**
- 저장소의 모든 커밋 히스토리 통합
- 파일별 전체 생명주기 추적 (생성 -> 수정 -> 삭제)
- 삭제된 파일 포함 가상 파일 시스템
- 브랜치 구조 및 머지 히스토리
- JSON (AI 최적화) 또는 TEXT (Human 친화) 형식
- 특정 파일의 전체 변경 이력 추적

**사용 예시:**
```bash
# AI Agent가 전체 컨텍스트를 JSON으로 받기
jevi
14  # Snapshot 선택
1   # JSON 형식
y   # 파일로 저장
context.json

# 특정 파일의 전체 히스토리 추적
jevi
14  # Snapshot
4   # 특정 파일 추적
src/Main.java
```

**AI Agent 활용 시나리오:**
- 코드베이스 전체 이해
- 버그 추적 및 분석
- 자동 문서화
- 리팩토링 지원
- Release Notes 생성

## 아키텍처

### 시스템 구조

```mermaid
graph TB
    subgraph "사용자 인터페이스"
        A[InteractiveMenu]
        B[TUIHelper]
    end
    
    subgraph "명령어 계층"
        C[StatusCommand]
        D[CommitCommand]
        E[PushCommand]
        F[BranchCommand]
        G[HistoryCommand]
        H[CommitExplorerCommand]
        I[SmartSearchCommand]
        J[SmartDiffCommand]
        K[SmartStashCommand]
        L[SnapshotCommand]
    end
    
    subgraph "핵심 로직"
        M[GitSafetyManager]
        N[ConflictResolver]
        O[SnapshotGenerator]
    end
    
    subgraph "Git 계층"
        P[JGit Library]
    end
    
    A --> C
    A --> D
    A --> E
    A --> F
    A --> G
    A --> H
    A --> I
    A --> J
    A --> K
    A --> L
    
    E --> M
    E --> N
    L --> O
    
    C --> P
    D --> P
    E --> P
    F --> P
    G --> P
    H --> P
    I --> P
    J --> P
    K --> P
    O --> P
    
    M --> P
    N --> P
    
    B -.제공.-> A
```

### 안전 시스템 작동 원리

```mermaid
sequenceDiagram
    participant User as 사용자
    participant Menu as InteractiveMenu
    participant Push as PushCommand
    participant Safety as GitSafetyManager
    participant Git as JGit
    participant Remote as 원격 저장소
    
    User->>Menu: [3] Push 선택
    Menu->>Push: execute()
    Push->>Safety: performSafetyCheck()
    
    Safety->>Git: fetch()
    Git->>Remote: 최신 정보 요청
    Remote-->>Git: 원격 상태 반환
    Git-->>Safety: Fetch 완료
    
    Safety->>Git: pull()
    Git->>Remote: 변경사항 요청
    Remote-->>Git: 최신 코드
    Git-->>Safety: Pull 완료
    
    alt 충돌 없음
        Safety-->>Push: 안전 (OK)
        Push->>Git: push()
        Git->>Remote: 로컬 커밋 전송
        Remote-->>Git: 성공
        Git-->>Push: Push 완료
        Push-->>Menu: 성공 메시지
        Menu-->>User: "Push 완료!"
    else 충돌 있음
        Safety-->>Push: 차단 (NOT OK)
        Push-->>Menu: 에러 메시지
        Menu-->>User: "충돌 있음 - [8] 충돌 해결 권장"
    end
```

### 충돌 해결 프로세스

```mermaid
stateDiagram-v2
    [*] --> 충돌감지
    충돌감지 --> 전략선택
    
    전략선택 --> OURS: 로컬 우선
    전략선택 --> THEIRS: 원격 우선
    전략선택 --> APPEND: 병합
    
    OURS --> 로컬버전적용
    THEIRS --> 원격버전적용
    APPEND --> 양쪽병합
    
    로컬버전적용 --> 자동스테이징
    원격버전적용 --> 자동스테이징
    양쪽병합 --> 자동스테이징
    
    자동스테이징 --> 해결완료
    해결완료 --> [*]
```

## 워크플로우

### 기본 워크플로우

```mermaid
flowchart TD
    A[시작] --> B[상태 확인]
    B --> C{변경사항 있음?}
    C -->|있음| D[커밋]
    C -->|없음| E[종료]
    D --> F[Push 요청]
    F --> G[안전 검사]
    G --> H{안전?}
    H -->|예| I[Push 실행]
    H -->|아니오| J[충돌 해결]
    J --> G
    I --> E
```

### 충돌 발생 시 워크플로우

```mermaid
flowchart TD
    A[Push 시도] --> B[자동 Fetch]
    B --> C[자동 Pull]
    C --> D{충돌?}
    D -->|없음| E[Push 성공]
    D -->|있음| F[Push 차단]
    F --> G[충돌 해결 메뉴]
    G --> H{전략 선택}
    H --> I[OURS]
    H --> J[THEIRS]
    H --> K[APPEND]
    I --> L[자동 해결]
    J --> L
    K --> L
    L --> M[커밋]
    M --> A
```

### 시간 여행 워크플로우

```mermaid
flowchart LR
    A[현재 브랜치] --> B[커밋 탐색기]
    B --> C[과거 커밋 선택]
    C --> D[시간 여행 실행]
    D --> E[과거 시점으로 이동]
    E --> F{작업}
    F --> G[코드 확인]
    F --> H[테스트]
    G --> I[원래 브랜치 복귀]
    H --> I
```

## 예제 시나리오

### 시나리오 1: 일반적인 작업 흐름

```
1. 코드 수정
2. jevi 실행
3. [1] 상태 확인 - 5개 파일 변경됨
4. [2] 커밋 - "기능 추가" 메시지
5. [3] Push - 자동으로 fetch+pull 후 안전하게 push
6. 완료!
```

### 시나리오 2: 충돌 발생 및 해결

```
1. [3] Push 시도
2. 시스템이 자동으로 fetch+pull
3. 충돌 감지! Push 차단됨
4. [8] 충돌 해결 선택
5. APPEND BOTH 전략 선택
6. 자동으로 충돌 해결 완료
7. [2] 커밋으로 병합 저장
8. [3] Push - 이제 성공!
```

### 시나리오 3: 과거 코드 탐색

```
1. [10] 커밋 탐색기
2. 3개월 전 커밋 선택
3. 변경된 파일 목록 확인
4. 특정 파일의 과거 내용 보기
5. [시간 여행] 실행
6. 과거 코드 상태로 이동하여 분석
7. 원래 브랜치로 복귀
```

### 시나리오 4: AI Agent가 저장소 분석

```
1. [14] 스냅샷 선택
2. JSON 형식 선택 (AI 최적화)
3. 파일로 저장: repo-context.json
4. AI Agent가 JSON 파싱:
   - 150개 커밋 분석
   - 5명의 기여자 식별
   - 파일 간 의존성 파악
   - 버그 패턴 발견
   - 자동 문서 생성
5. AI가 인사이트 제공:
   "Main.java는 지난 3개월간 15번 수정되었으며,
    주로 버그 수정이 많았습니다. 리팩토링 권장합니다."
```

## 기술 스택

- **언어**: Java 17
- **빌드 도구**: Maven 3.8+
- **Git 라이브러리**: JGit 6.7
- **CLI 프레임워크**: Picocli 4.7
- **터미널 출력**: JANSI 2.4

## 빌드 및 배포

### 개발 빌드

```bash
mvn clean package
```

### 릴리스 빌드

**Linux/macOS:**
```bash
./build-release.sh
```

**Windows:**
```cmd
build-release.bat
```

생성 파일:
- `release/supergit-jevi-{version}-unix.tar.gz`
- `release/supergit-jevi-{version}-windows.zip`

## 트러블슈팅

### "java: command not found"
- Java가 설치되지 않았거나 PATH에 없음
- Java 17 설치 후 터미널 재시작

### "UnsupportedClassVersionError"
- Java 버전이 낮음
- Java 17 이상으로 업그레이드

### "Git 저장소를 찾을 수 없습니다"
- 현재 디렉토리가 Git 저장소가 아님
- `git init` 실행 또는 [9] 메뉴 사용

### Windows에서 실행이 안됨
- PATH에 Java가 없을 수 있음
- 전체 경로로 시도: `java -jar C:\path\to\supergit-jevi.jar`

## 기여하기

이슈 및 Pull Request는 환영합니다!

## 라이선스

MIT License - [LICENSE](LICENSE) 파일 참조

Copyright (c) 2026 Rheehose

---

**SuperGit-Jevi - 제비처럼 빠르게, 하지만 안전하게**
