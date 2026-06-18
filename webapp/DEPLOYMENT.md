# SuperGit-Jevi Web 배포 가이드

## Vercel 배포

### 방법 1: Vercel CLI (권장)

1. **Vercel CLI 설치**
```bash
npm install -g vercel
```

2. **로그인**
```bash
vercel login
```

3. **프로젝트 배포**
```bash
# webapp 디렉토리에서 실행
cd webapp
vercel
```

4. **프로덕션 배포**
```bash
vercel --prod
```

### 방법 2: GitHub 연동

1. **GitHub에 푸시**
```bash
git add .
git commit -m "feat: add web version"
git push origin main
```

2. **Vercel 대시보드**
   - https://vercel.com 접속
   - "Import Project" 클릭
   - GitHub 저장소 선택
   - Root Directory: `webapp`
   - Build Command: `npm run build`
   - Output Directory: `.next`

3. **환경 변수 설정** (선택사항)
   - `NEXT_PUBLIC_APP_NAME`: SuperGit-Jevi Web
   - `NEXT_PUBLIC_APP_VERSION`: 1.0.0

4. **Deploy** 클릭

### 방법 3: Drag & Drop

1. **빌드**
```bash
cd webapp
npm run build
```

2. **Vercel 대시보드**
   - https://vercel.com/new
   - `.next` 폴더를 드래그 앤 드롭

## 배포 설정

### vercel.json

프로젝트 루트와 webapp 폴더에 `vercel.json` 파일이 설정되어 있습니다.

**루트 vercel.json** (모노레포 구조):
```json
{
  "version": 2,
  "name": "supergit-jevi-web",
  "builds": [
    {
      "src": "webapp/package.json",
      "use": "@vercel/next"
    }
  ],
  "routes": [
    {
      "src": "/(.*)",
      "dest": "webapp/$1"
    }
  ]
}
```

**webapp/vercel.json** (단독 배포):
```json
{
  "version": 2,
  "framework": "nextjs",
  "buildCommand": "npm run build",
  "outputDirectory": ".next"
}
```

## 도메인 설정

### 커스텀 도메인 추가

1. Vercel 대시보드 → 프로젝트 선택
2. Settings → Domains
3. 도메인 입력 (예: supergit.yourdomain.com)
4. DNS 설정 추가:
   - Type: CNAME
   - Name: supergit (또는 @)
   - Value: cname.vercel-dns.com

### SSL 인증서

- Vercel이 자동으로 Let's Encrypt SSL 인증서 발급
- HTTPS 자동 활성화

## 환경별 배포

### Preview 배포
```bash
# 브랜치별 미리보기
git checkout -b feature/new-feature
git push origin feature/new-feature
# Vercel이 자동으로 preview URL 생성
```

### Production 배포
```bash
# main 브랜치에 푸시하면 자동 배포
git checkout main
git merge feature/new-feature
git push origin main
```

## 성능 최적화

### Edge Functions

Vercel의 Edge Functions를 활용하여 전 세계적으로 빠른 응답:

```json
// vercel.json에 추가
{
  "functions": {
    "app/**/*.tsx": {
      "runtime": "edge"
    }
  }
}
```

### Analytics

Vercel Analytics 활성화:

1. Vercel 대시보드 → Analytics
2. Enable 클릭
3. 코드에 추가:
```tsx
// app/layout.tsx
import { Analytics } from '@vercel/analytics/react';

export default function RootLayout({ children }) {
  return (
    <html>
      <body>
        {children}
        <Analytics />
      </body>
    </html>
  );
}
```

## 모니터링

### Vercel Logs

```bash
# 로그 확인
vercel logs
```

### Error Tracking

Vercel 대시보드에서 에러 추적:
- Errors 탭에서 실시간 에러 확인
- Stack trace 분석

## 롤백

### 이전 배포로 되돌리기

1. Vercel 대시보드 → Deployments
2. 이전 배포 선택
3. "Promote to Production" 클릭

## 기타 호스팅 옵션

### Netlify

```bash
# netlify.toml 생성
[build]
  base = "webapp"
  command = "npm run build"
  publish = ".next"

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200
```

### GitHub Pages

```bash
# next.config.js 수정
module.exports = {
  output: 'export',
  basePath: '/supergit-jevi-web',
  images: {
    unoptimized: true,
  },
};

# 빌드 및 배포
npm run build
npm run export
# out/ 폴더를 gh-pages 브랜치로 푸시
```

### Cloudflare Pages

1. Cloudflare 대시보드
2. Pages → Create a project
3. GitHub 연동
4. Build settings:
   - Framework: Next.js
   - Build command: `npm run build`
   - Build output: `.next`

## 트러블슈팅

### 빌드 실패

**문제**: 타입스크립트 에러
```bash
# 로컬에서 먼저 확인
npm run build
```

**해결**: 타입 에러 수정 후 재배포

### 환경 변수 누락

**문제**: 환경 변수 접근 불가
**해결**: Vercel 대시보드에서 환경 변수 추가

### 메모리 부족

**문제**: Build exceeded maximum memory
**해결**: 
```json
// vercel.json
{
  "functions": {
    "app/**/*.tsx": {
      "memory": 3008
    }
  }
}
```

## 참고 링크

- [Vercel Documentation](https://vercel.com/docs)
- [Next.js Deployment](https://nextjs.org/docs/deployment)
- [Vercel CLI](https://vercel.com/docs/cli)

---

**문서 버전**: 1.0.0  
**최종 업데이트**: 2026년 6월 18일
