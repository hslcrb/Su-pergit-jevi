package com.supergit.jevi.core;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Set;

/**
 * 자동 충돌 해결 매니저
 * 충돌 발생 시 자동으로 해결 시도
 */
public class ConflictResolver {
    
    private final Git git;
    
    public enum ResolveStrategy {
        ACCEPT_OURS,    // 로컬 변경사항 우선
        ACCEPT_THEIRS,  // 원격 변경사항 우선
        APPEND_BOTH,    // 둘 다 병합 (더미 공백 추가)
        MANUAL          // 수동 해결 필요
    }
    
    public ConflictResolver(Git git) {
        this.git = git;
    }
    
    /**
     * 충돌 자동 해결 시도
     */
    public boolean autoResolve(ResolveStrategy strategy) {
        try {
            Status status = git.status().call();
            Set<String> conflicting = status.getConflicting();
            
            if (conflicting.isEmpty()) {
                TUIHelper.printInfo("충돌이 없습니다.");
                return true;
            }
            
            TUIHelper.printWarning("충돌 파일 " + conflicting.size() + "개 발견");
            TUIHelper.printInfo("해결 전략: " + strategy.name());
            
            int resolved = 0;
            
            for (String file : conflicting) {
                boolean success = false;
                
                switch (strategy) {
                    case ACCEPT_OURS -> success = resolveAcceptOurs(file);
                    case ACCEPT_THEIRS -> success = resolveAcceptTheirs(file);
                    case APPEND_BOTH -> success = resolveAppendBoth(file);
                    case MANUAL -> {
                        TUIHelper.printInfo("수동 해결 필요: " + file);
                        success = false;
                    }
                }
                
                if (success) {
                    resolved++;
                    TUIHelper.printSuccess("해결됨: " + file);
                }
            }
            
            if (resolved == conflicting.size()) {
                TUIHelper.printSuccess("모든 충돌이 해결되었습니다!");
                
                // 해결된 파일을 스테이징
                for (String file : conflicting) {
                    git.add().addFilepattern(file).call();
                }
                
                return true;
            } else {
                TUIHelper.printWarning(resolved + "/" + conflicting.size() + " 파일 해결됨");
                return false;
            }
            
        } catch (Exception e) {
            TUIHelper.printError("충돌 해결 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 로컬 버전 선택
     */
    private boolean resolveAcceptOurs(String filePath) {
        try {
            git.checkout()
                .setStage(org.eclipse.jgit.api.CheckoutCommand.Stage.OURS)
                .addPath(filePath)
                .call();
            return true;
        } catch (Exception e) {
            TUIHelper.printError("OURS 해결 실패: " + filePath);
            return false;
        }
    }
    
    /**
     * 원격 버전 선택
     */
    private boolean resolveAcceptTheirs(String filePath) {
        try {
            git.checkout()
                .setStage(org.eclipse.jgit.api.CheckoutCommand.Stage.THEIRS)
                .addPath(filePath)
                .call();
            return true;
        } catch (Exception e) {
            TUIHelper.printError("THEIRS 해결 실패: " + filePath);
            return false;
        }
    }
    
    /**
     * 두 버전을 병합 (더미 공백 추가)
     */
    private boolean resolveAppendBoth(String filePath) {
        try {
            File repoDir = git.getRepository().getWorkTree();
            File file = new File(repoDir, filePath);
            
            if (!file.exists()) {
                return false;
            }
            
            // 충돌 마커 제거하고 양쪽 내용 유지
            String content = Files.readString(file.toPath());
            
            // 충돌 마커를 기준으로 분리
            String resolved = content
                .replaceAll("<<<<<<< HEAD\n", "")
                .replaceAll("=======\n", "\n/* --- 병합된 변경사항 --- */\n")
                .replaceAll(">>>>>>> .*\n", "\n/* --- 병합 완료 --- */\n");
            
            // 더미 공백 추가 (타임스탬프 주석)
            resolved += "\n\n/* Auto-resolved at " + 
                java.time.LocalDateTime.now() + " */\n";
            
            // 파일 저장
            Files.writeString(file.toPath(), resolved);
            
            return true;
            
        } catch (IOException e) {
            TUIHelper.printError("APPEND 해결 실패: " + filePath);
            return false;
        }
    }
    
    /**
     * 충돌 검사
     */
    public boolean hasConflicts() {
        try {
            Status status = git.status().call();
            return !status.getConflicting().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 충돌 파일 목록
     */
    public Set<String> getConflictingFiles() {
        try {
            Status status = git.status().call();
            return status.getConflicting();
        } catch (Exception e) {
            return Set.of();
        }
    }
}
