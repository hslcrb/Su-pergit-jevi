# SuperGit-Jevi Web

**브라우저에서 실행되는 슈퍼깃 - 제비처럼 빠른 웹 Git 도구**

SuperGit-Jevi의 웹 버전입니다. 서버 없이 브라우저에서 직접 실행되며, File System Access API를 통해 로컬 폴더에 접근합니다.

## 주요 특징

### 완전한 클라이언트 사이드

- ✅ **서버 불필요**: 모든 작업이 브라우저에서 실행
- ✅ **로컬 파일 접근**: File System Access API로 실제 폴더 작업
- ✅ **순수 JavaScript Git**: isomorphic-git 사용
- ✅ **터미널 UI**: xterm.js로 완벽한 터미널 경험
- ✅ **안전 모드**: Java 버전과 동일한 안전 시스템

### 지원 기능

- 📊 Git Status - 변경사항 확인
- 💾 Git Commit - 커밋하기
- 🚀 Git Push - 안전 모드 (자동 fetch+pull)
- 🌿 Branch 관리 - 생성, 전환, 목록
- 📜 History - 커밋 로그
- ⬇️ Git Pull - 최신 변경사항 받기
- 🆕 Git Init - 저장소 초기화

## 시스템 요구사항

### 브라우저 호환성

- ✅ **Chrome/Edge** 86 이상
- ✅ **Safari** 15.2 이상
- ⚠️ **Firefox** - File System Access API 미지원 (현재 사용 불가)

### 필수 API

- File System Access API
- IndexedDB
- Web Workers (권장)

## 설치 및 실행

### 1. 의존성 설치

\`\`\`bash
cd webapp
npm install
\`\`\`

### 2. 개발 서버 실행

\`\`\`bash
npm run dev
\`\`\`

브라우저에서 http://localhost:3000 열기

### 3. 프로덕션 빌드

\`\`\`bash
npm run build
npm run start
\`\`\`

### 4. 정적 배포

\`\`\`bash
npm run build
# out/ 폴더를 정적 호스팅 서비스에 배포
\`\`\`

## 사용 방법

### 기본 워크플로우

1. **폴더 선택**
   - 우측 상단 "📁 폴더 선택" 버튼 클릭
   - 또는 터미널에 `select` 입력
   - Git 저장소 또는 일반 폴더 선택

2. **Git 작업**
   - 터미널에 명령어 입력
   - 또는 하단 버튼 클릭

3. **명령어 예시**
   \`\`\`
   status              # 상태 확인
   commit "메시지"     # 커밋
   push                # 안전 모드 Push
   branches            # 브랜치 목록
   log                 # 히스토리
   help                # 도움말
   \`\`\`

### 명령어 목록

#### 기본 명령어

\`\`\`
select              - 폴더 선택
menu                - 메뉴 표시
help, h             - 도움말
\`\`\`

#### Git 명령어

\`\`\`
1, status           - 상태 확인
2, commit <msg>     - 커밋 (예: commit "기능 추가")
3, push             - 안전 모드 Push
4, branches         - 브랜치 목록
5, log, history     - 커밋 히스토리
6, pull             - Pull

init                - Git 저장소 초기화
branch <name>       - 브랜치 생성
checkout <ref>      - 브랜치 전환
\`\`\`

## 기술 스택

- **Framework**: Next.js 14 (App Router)
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **Git Engine**: isomorphic-git (순수 JavaScript Git 구현)
- **Terminal**: xterm.js + FitAddon
- **File System**: File System Access API

## 아키텍처

\`\`\`mermaid
graph TD
    A[Next.js App] --> B[Terminal Component]
    A --> C[Git Utils]
    A --> D[FS Adapter]
    
    B --> E[xterm.js]
    C --> F[isomorphic-git]
    D --> G[File System Access API]
    
    F --> D
    G --> H[Local Folder]
\`\`\`

### 주요 컴포넌트

1. **Terminal.tsx**
   - xterm.js 기반 터미널 UI
   - 명령어 입력 처리
   - 컬러 출력

2. **git-utils.ts**
   - isomorphic-git 래퍼
   - Git 작업 추상화
   - 안전 모드 구현

3. **fs-adapter.ts**
   - File System Access API 어댑터
   - isomorphic-git 호환 인터페이스

## 제한사항

### 브라우저 제한

- **Firefox**: File System Access API 미지원
- **Safari**: 일부 기능 제한 가능
- **모바일**: File System Access API 지원 제한적

### Git 기능 제한

- **SSH 인증**: 미지원 (HTTPS만 가능)
- **대용량 저장소**: 성능 이슈 가능
- **LFS**: 미지원
- **Submodules**: 제한적 지원

### 워크어라운드

- **원격 저장소 접근**: CORS Proxy 사용 (https://cors.isomorphic-git.org)
- **인증**: Personal Access Token 권장
- **대용량 파일**: 브라우저 메모리 제한 고려

## 보안

### 데이터 저장

- 모든 데이터는 **로컬**에만 저장됨
- 서버로 전송되지 않음
- IndexedDB에 Git 객체 저장

### 권한

- File System Access API 권한 필요
- 사용자가 명시적으로 폴더 선택
- 선택한 폴더에만 접근 가능

## 개발

### 프로젝트 구조

\`\`\`
webapp/
├── app/
│   ├── page.tsx          # 메인 페이지
│   ├── layout.tsx        # 레이아웃
│   └── globals.css       # 전역 스타일
├── components/
│   └── Terminal.tsx      # 터미널 컴포넌트
├── lib/
│   ├── git-utils.ts      # Git 유틸리티
│   └── fs-adapter.ts     # 파일 시스템 어댑터
└── public/               # 정적 파일
\`\`\`

### 개발 서버 실행

\`\`\`bash
npm run dev
\`\`\`

### 타입 체크

\`\`\`bash
npm run type-check
\`\`\`

### 린트

\`\`\`bash
npm run lint
\`\`\`

## 배포

### Vercel (권장)

\`\`\`bash
# Vercel CLI 설치
npm install -g vercel

# 배포
vercel
\`\`\`

### 정적 배포

\`\`\`bash
# 빌드
npm run build

# out/ 폴더를 GitHub Pages, Netlify, Cloudflare Pages 등에 배포
\`\`\`

### Docker

\`\`\`dockerfile
FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
\`\`\`

## 트러블슈팅

### "showDirectoryPicker is not defined"

- File System Access API 미지원 브라우저
- Chrome/Edge 86+ 또는 Safari 15.2+ 사용

### CORS 오류

- isomorphic-git의 CORS Proxy 사용 중
- 자체 서버 배포 시 CORS 헤더 설정 필요

### 메모리 부족

- 대용량 저장소는 브라우저 메모리 제한
- 작은 저장소 또는 shallow clone 권장

### IndexedDB 오류

- 브라우저 저장소 공간 확인
- 시크릿 모드에서는 제한적

## Java 버전과의 차이

| 기능 | Java 버전 | Web 버전 |
|------|----------|----------|
| 실행 환경 | 터미널 | 브라우저 |
| Git 엔진 | JGit | isomorphic-git |
| 파일 접근 | 직접 | File System Access API |
| 인증 | SSH/HTTPS | HTTPS (Token) |
| 대용량 저장소 | 제한 없음 | 브라우저 제한 |
| 네트워크 | 직접 | CORS Proxy 필요 |
| 플랫폼 | 모든 OS | 지원 브라우저만 |

## 로드맵

- [ ] Drag & Drop 파일 추가
- [ ] Visual Diff 뷰어
- [ ] Branch 그래프 시각화
- [ ] Stash 지원
- [ ] Cherry-pick 지원
- [ ] 커밋 탐색기
- [ ] 코드 검색
- [ ] PWA 지원
- [ ] 오프라인 모드

## 라이선스

MIT License - [LICENSE](../LICENSE) 파일 참조

## 관련 프로젝트

- [SuperGit-Jevi](../) - Java TUI 버전
- [isomorphic-git](https://isomorphic-git.org/) - 순수 JavaScript Git
- [xterm.js](https://xtermjs.org/) - 터미널 에뮬레이터

---

**SuperGit-Jevi Web - 브라우저에서도 제비처럼 빠르게!**
