package com.supergit.jevi.commands;

import com.supergit.jevi.core.GitSafetyManager;
import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.transport.PushResult;

/**
 * Push 명령 (안전 장치 포함, 최적화됨)
 */
public class PushCommand {
    private final Git git;
    private final boolean force;
    
    public PushCommand(Git git, boolean force) {
        this.git = git;
        this.force = force;
    }
    
    public void execute() {
        try {
            TUIHelper.printHeader("Push 실행", "원격 저장소로 업로드");

            if (!force) {
                GitSafetyManager safetyManager = new GitSafetyManager(git);
                GitSafetyManager.SafetyCheckResult result = safetyManager.performSafetyCheck();

                if (!result.safeToProcceed) {
                    TUIHelper.printError("Push 차단: " + result.message);
                    TUIHelper.printInfo("문제를 해결한 후 다시 시도하세요.");
                    return;
                }
            }

            TUIHelper.printStep("원격 저장소로 Push 중...");
            
            PushCommand pushCommand = git.push();
            Iterable<PushResult> results = pushCommand.call();
            
            for (PushResult result : results) {
                if (result.getMessages() != null && !result.getMessages().isEmpty()) {
                    TUIHelper.printInfo("서버 메시지: " + result.getMessages());
                }
            }

            TUIHelper.printSuccess("Push 완료!");

        } catch (Exception e) {
            TUIHelper.printError("Push 실패: " + e.getMessage());
        }
    }
}
