# AI Agent Snapshot 기능 기술 명세서

## 개요

AI Agent가 Git 저장소의 전체 히스토리와 현재 상태를 하나의 통합된 뷰로 이해할 수 있도록 하는 기능입니다.

## 명령어

```
snapshot [options]
```

**별칭**: `snap`, `ai-context`

## 목적

AI Agent가 다음을 한 번에 파악할 수 있도록:
1. 모든 커밋 히스토리 (메시지, 설명, 작성자, 날짜)
2. 각 커밋의 변경 파일 목록
3. 파일별 전체 진화 과정 (생성 -> 수정 -> 현재)
4. 브랜치 구조 및 머지 히스토리
5. 현재 파일 시스템 상태
6. 가상 파일 시스템 (삭제된 파일 포함)

## 출력 형식

### JSON 형식 (AI 최적화)

```json
{
  "repository": {
    "name": "project-name",
    "currentBranch": "main",
    "totalCommits": 150
  },
  "commits": [
    {
      "hash": "abc123...",
      "shortHash": "abc123",
      "author": "John Doe",
      "email": "john@example.com",
      "date": "2026-06-18T15:00:00+09:00",
      "message": "커밋 제목",
      "description": "상세 설명\n여러 줄...",
      "parents": ["def456..."],
      "mergeCommit": false,
      "changedFiles": [
        {
          "path": "src/Main.java",
          "status": "MODIFIED",
          "additions": 10,
          "deletions": 5
        }
      ]
    }
  ],
  "fileSystem": {
    "current": {
      "src/Main.java": {
        "exists": true,
        "size": 1024,
        "lastModified": "2026-06-18T15:00:00+09:00"
      }
    },
    "history": {
      "src/Main.java": [
        {
          "commit": "abc123",
          "action": "CREATED",
          "date": "2026-01-01T00:00:00+09:00"
        },
        {
          "commit": "def456",
          "action": "MODIFIED",
          "date": "2026-03-15T12:00:00+09:00"
        }
      ]
    },
    "deleted": {
      "old/Legacy.java": {
        "deletedIn": "xyz789",
        "deletedDate": "2026-05-01T00:00:00+09:00",
        "existedFor": "120 days"
      }
    }
  },
  "branches": {
    "all": ["main", "develop", "feature/ai"],
    "merged": ["feature/old"],
    "active": ["main", "develop"]
  },
  "statistics": {
    "totalFiles": 45,
    "activeFiles": 40,
    "deletedFiles": 5,
    "totalCommits": 150,
    "contributors": 5
  }
}
```

### 텍스트 형식 (Human 친화적)

```
=== Repository Snapshot ===
Repository: project-name
Branch: main
Total Commits: 150

=== Commit History ===

[1] abc123 - 2026-06-18 15:00:00
Author: John Doe <john@example.com>
Message: 커밋 제목

상세 설명이 여기 있습니다.
여러 줄로 작성 가능합니다.

Changed Files:
  M src/Main.java (+10, -5)
  A src/New.java (+50, -0)
  D old/Legacy.java (+0, -30)

---

=== File Evolution ===

src/Main.java:
  [CREATED] abc111 (2026-01-01) Initial commit
  [MODIFIED] def222 (2026-03-15) Add feature
  [MODIFIED] ghi333 (2026-06-18) Fix bug
  [CURRENT] 1024 bytes

src/New.java:
  [CREATED] abc123 (2026-06-18) Add new feature
  [CURRENT] 2048 bytes

old/Legacy.java:
  [CREATED] old111 (2025-01-01)
  [MODIFIED] old222 (2025-03-15)
  [DELETED] xyz789 (2026-05-01) Removed legacy code
  [EXISTED FOR] 120 days

=== Branch Structure ===

main (150 commits)
  |- feature/ai (merged at def456)
  |- feature/old (merged at xyz789)
develop (80 commits)
  |- feature/wip (active)

=== Statistics ===
Total Files: 45 (40 active, 5 deleted)
Total Commits: 150
Contributors: 5
Repository Age: 180 days
```

## 옵션

- `--format=json` : JSON 형식 출력 (AI 최적화)
- `--format=text` : 텍스트 형식 출력 (Human 친화적, 기본값)
- `--full` : 전체 파일 내용 포함 (매우 큰 출력)
- `--file=<path>` : 특정 파일의 전체 히스토리만
- `--since=<date>` : 특정 날짜 이후만
- `--branch=<name>` : 특정 브랜치만
- `--output=<file>` : 파일로 저장

## 사용 예시

### AI Agent 용도
```bash
# 전체 컨텍스트를 JSON으로
jevi snapshot --format=json > context.json

# 특정 파일의 전체 역사
jevi snapshot --file=src/Main.java --format=json

# 최근 1개월 변경사항만
jevi snapshot --since=2026-05-01 --format=json
```

### Human 용도
```bash
# 읽기 쉬운 텍스트 형식
jevi snapshot

# 특정 파일 추적
jevi snapshot --file=src/Main.java

# 파일로 저장
jevi snapshot --output=repo-snapshot.txt
```

## 기술 구현

### 데이터 수집 단계

1. **커밋 히스토리 수집**
   - `git log --all --full-history`
   - 모든 브랜치, 모든 커밋 포함
   - 머지 커밋 특별 표시

2. **파일 변경 추적**
   - 각 커밋의 `DiffEntry` 분석
   - 파일별 생성/수정/삭제 이벤트 기록

3. **브랜치 구조 분석**
   - `git branch --all`
   - 브랜치 간 머지 관계 파악

4. **현재 상태 스캔**
   - 작업 디렉토리의 모든 파일
   - 파일 크기, 최종 수정 시간

5. **가상 파일 시스템 구축**
   - 현재 존재하는 파일
   - 과거에 존재했던 파일 (삭제됨)
   - 각 파일의 전체 생명주기

### 출력 생성 단계

1. **데이터 집계**
   - 모든 정보를 메모리 내 구조로 통합

2. **포맷 변환**
   - JSON: 구조화된 데이터
   - TEXT: Human-readable 형식

3. **최적화**
   - 대용량 저장소를 위한 스트리밍
   - 메모리 효율적 처리

## 성능 고려사항

- 커밋 1000개 이하: < 5초
- 커밋 1000-5000개: < 30초
- 커밋 5000개 이상: 진행률 표시

## AI Agent 활용 시나리오

1. **코드베이스 이해**
   - 전체 프로젝트 히스토리를 한 번에 파악
   - 파일 간 관계 이해

2. **버그 추적**
   - 특정 파일의 변경 히스토리 분석
   - 버그 도입 시점 파악

3. **리팩토링 지원**
   - 파일의 진화 과정 이해
   - 삭제된 코드 복구

4. **문서화**
   - 자동 변경 로그 생성
   - Release Notes 작성

## 확장 가능성

- Git LFS 파일 지원
- Submodule 정보 포함
- Git hooks 정보
- CI/CD 통합 정보
