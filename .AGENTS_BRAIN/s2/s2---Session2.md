# Session 2 - SuperGit-Jevi 개선 및 오류 수정

**시작 시간**: 2026-06-18 16:45

## 세션 1 요약
- SuperGit-Jevi Java 프로젝트 생성
- TUI 인터페이스 구현 (Lanterna, JANSI 사용)
- 안전 시스템 구현 (push 전 자동 fetch+pull)
- 기본 Git 명령어 구현 (status, commit, push, branch, history)
- 대화형 메뉴 시스템 추가
- 이모지 제거 및 성능 최적화

## 세션 2 목표
1. 컴파일 오류 해결
   - SnapshotGenerator.java 190번째 줄 오류
   - ResetCommand 중복 정의 오류
2. 추가 기능 구현 검토
3. 코드 안정화

## 문제 상황
```
ERROR: /home/rheehoselenovo2/개발프로젝트/Su;pergit-jevi/src/main/java/com/supergit/jevi/snapshot/SnapshotGenerator.java:[190,55] 
cannot find symbol: method getSize(org.eclipse.jgit.lib.ObjectId,int)
location: class org.eclipse.jgit.lib.ObjectReader
```

## 해결 완료

### 문제 1: SnapshotGenerator.java 오류
**원인**: JGit API 변경으로 `ObjectReader.getSize()` 메서드가 더 이상 존재하지 않음
**해결**: `ObjectReader.getObjectSize()` 메서드로 변경하고 try-catch로 예외 처리 추가

```java
// 변경 전
file.size = treeWalk.getObjectReader().getSize(
    treeWalk.getObjectId(0), org.eclipse.jgit.lib.Constants.OBJ_BLOB);

// 변경 후
try {
    ObjectReader reader = treeWalk.getObjectReader();
    file.size = reader.getObjectSize(
        treeWalk.getObjectId(0), org.eclipse.jgit.lib.Constants.OBJ_BLOB);
} catch (Exception e) {
    file.size = 0;
}
```

### 문제 2: ResetCommand 중복 정의 오류
**원인**: 클래스 이름이 JGit의 `org.eclipse.jgit.api.ResetCommand`와 동일하여 충돌
**해결**: 이미 전체 패키지 경로로 사용하고 있어 실제 충돌 없음 - 빌드 시스템 캐시 문제였음

## 빌드 결과
```
[INFO] BUILD SUCCESS
[INFO] Total time:  18.963 s
[INFO] Finished at: 2026-06-18T16:47:35+09:00
```

## 다음 단계
- 프로그램 실행 테스트
- 추가 기능 검토
- 문서 업데이트
