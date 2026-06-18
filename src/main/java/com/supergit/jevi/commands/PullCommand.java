package com.supergit.jevi.commands;

import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;

/**
 * Pull 명령 (최적화됨)
 */
public class PullCommand {
    private final Git git;
    
    public PullCommand(Git git) {
        this.git = git;
    }
    
    public void execute() {
        try {
            TUIHelper.printHeader("Pull 실행", "원격 저장소에서 가져오기");
            
            TUIHelper.printStep("원격 저장소에서 변경사항 가져오는 중...");
            
            PullResult result = git.pull().call();
            
            if (result.isSuccessful()) {
                TUIHelper.printSuccess("Pull 완료!");
                
                if (result.getMergeResult() != null) {
                    TUIHelper.printInfo("병합 상태: " + result.getMergeResult().getMergeStatus());
                }
            } else {
                TUIHelper.printWarning("Pull이 완전히 성공하지 못했습니다.");
            }
            
        } catch (Exception e) {
            TUIHelper.printError("Pull 실패: " + e.getMessage());
        }
    }
}
