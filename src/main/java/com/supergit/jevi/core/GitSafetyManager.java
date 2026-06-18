package com.supergit.jevi.core;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.FetchResult;

/**
 * Git 안전 관리자
 * push 전에 반드시 fetch + pull을 강제하는 안전장치
 */
public class GitSafetyManager {
    
    private final Git git;
    
    public GitSafetyManager(Git git) {
        this.git = git;
    }
    
    /**
     * 안전한 push를 위한 사전 검사 및 동기화
     * @return true if safe to push, false otherwise
     */
    public SafetyCheckResult performSafetyCheck() {
        SafetyCheckResult result = new SafetyCheckResult();
        
        try {
            TUIHelper.printBox("🛡️ 안전 검사 시작", TUIHelper.BoxStyle.INFO);
            
            // 1. Fetch 먼저
            TUIHelper.printStep("1️⃣ 원격 저장소 확인 중 (fetch)...");
            FetchResult fetchResult = git.fetch().call();
            result.fetchSuccess = true;
            TUIHelper.printSuccess("✅ Fetch 완료");
            
            // 2. 원격과 로컬 차이 확인
            Repository repo = git.getRepository();
            String currentBranch = repo.getBranch();
            
            // 3. Pull 시도
            TUIHelper.printStep("2️⃣ 최신 변경사항 받기 (pull)...");
            PullResult pullResult = git.pull().call();
            result.pullSuccess = pullResult.isSuccessful();
            
            if (result.pullSuccess) {
                TUIHelper.printSuccess("✅ Pull 완료");
                
                if (pullResult.getMergeResult() != null) {
                    if (pullResult.getMergeResult().getMergeStatus().isSuccessful()) {
                        TUIHelper.printInfo("📦 병합 완료: " + 
                            pullResult.getMergeResult().getMergeStatus());
                    } else {
                        TUIHelper.printWarning("⚠️ 병합 충돌 발생!");
                        result.hasConflicts = true;
                        result.message = "병합 충돌을 해결한 후 다시 시도하세요.";
                    }
                }
            } else {
                TUIHelper.printError("❌ Pull 실패");
                result.message = "원격 저장소에서 변경사항을 가져오지 못했습니다.";
            }
            
            // 4. 최종 판단
            if (result.fetchSuccess && result.pullSuccess && !result.hasConflicts) {
                TUIHelper.printBox("✅ 안전 검사 통과 - Push 가능", TUIHelper.BoxStyle.SUCCESS);
                result.safeToProcceed = true;
            } else {
                TUIHelper.printBox("❌ 안전 검사 실패 - Push 차단", TUIHelper.BoxStyle.ERROR);
                result.safeToProcceed = false;
            }
            
        } catch (Exception e) {
            TUIHelper.printError("❌ 안전 검사 중 오류: " + e.getMessage());
            result.safeToProcceed = false;
            result.message = e.getMessage();
        }
        
        return result;
    }
    
    /**
     * 안전 검사 결과
     */
    public static class SafetyCheckResult {
        public boolean fetchSuccess = false;
        public boolean pullSuccess = false;
        public boolean hasConflicts = false;
        public boolean safeToProcceed = false;
        public String message = "";
    }
}
