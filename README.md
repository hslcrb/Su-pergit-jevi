# 🐦 SuperGit-Jevi

**슈퍼하게 Git을 사용하는 제비처럼 빠른 Java 도구**

## 왜 SuperGit-Jevi인가?

- **슈퍼깃**: Git을 슈퍼롭게 쓸 수 있어서 슈퍼깃
- **제비(Jevi)**: 제비처럼 빠르고 민첩하게! (원래는 자바(Java)였지만 제비가 더 좋아서 제비로 변신)

## 특징

✨ **직관적인 명령어**: 복잡한 Git 명령을 간단하게  
🎨 **예쁜 출력**: 이모지와 함께하는 깔끔한 인터페이스  
⚡ **빠른 실행**: 제비처럼 날쌔게!  
🔧 **슈퍼 기능들**: Git의 모든 기능을 더 쉽게

## 설치

```bash
# Maven으로 빌드
mvn clean package

# 실행
java -jar target/supergit-jevi-1.0.0.jar
```

## 사용법

### 기본 명령어

```bash
# 상태 확인 (슈퍼하게!)
jevi status

# 커밋 (슈퍼하게!)
jevi commit "메시지" -a

# 빠른 커밋 (타임스탬프 자동 추가)
jevi commit "작업 완료" -a -q

# 브랜치 목록
jevi branch -l

# 새 브랜치 생성
jevi branch feature/awesome -c

# 커밋 히스토리
jevi history

# 최근 5개 커밋만
jevi history -n 5

# 도움말
jevi --help
```

## 명령어 상세

### `jevi status`
- 저장소의 현재 상태를 예쁘게 표시
- 수정/추가/삭제된 파일을 이모지로 구분
- `-v`: 상세 정보 표시

### `jevi commit <message>`
- 스마트한 커밋 기능
- `-a, --all`: 모든 변경사항 자동 스테이징
- `-q, --quick`: 타임스탬프 자동 추가

### `jevi branch`
- `-l, --list`: 브랜치 목록 표시
- `-c, --create`: 새 브랜치 생성
- `-d, --delete`: 브랜치 삭제

### `jevi history`
- 커밋 히스토리를 깔끔하게 표시
- `-n <number>`: 표시할 커밋 개수
- `-a, --all`: 모든 커밋 표시

## 기술 스택

- **Java 17**: 모던 자바
- **JGit**: Git 작업을 위한 라이브러리
- **Picocli**: 강력한 CLI 프레임워크
- **Maven**: 빌드 도구

## 라이센스

MIT License - 자유롭게 사용하세요!

---

**제비처럼 빠르게 날아올라라! 🐦**
