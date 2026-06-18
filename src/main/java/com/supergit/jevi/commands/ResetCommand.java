package com.supergit.jevi.commands;

import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;

/**
 * Reset 명령 - 커밋 되돌리기/삭제
 */
public class ResetCommand {
    private final Git git;
    private final int commitCount;
    private final boolean hard;
    
    public ResetCommand(Git git, int commitCount, boolean hard) {
        this.git = git;
        this.commitCount = commitCount;
        this.hard = hard;
    }
    
    public void execute() {
        try {
            TUIHelper.printHeader("커밋 되돌리기", "이전 상태로 되돌립니다");
            
            if (hard) {
                TUIHelper.printWarning("HARD 모드: 모든 변경사항이 완전히 삭제됩니다!");
                TUIHelper.printWarning("되돌릴 수 없습니다!");
            } else {
                TUIHelper.printInfo("SOFT 모드: 변경사항은 유지되고 커밋만 취소됩니다");
            }
            
            TUIHelper.printInfo(commitCount + "개 커밋을 되돌립니다");
            
            // HEAD~N으로 되돌리기
            String target = "HEAD~" + commitCount;
            
            TUIHelper.printStep("되돌리는 중...");
            
            Ref ref = git.reset()
                .setMode(hard ? org.eclipse.jgit.api.ResetCommand.ResetType.HARD : 
                              org.eclipse.jgit.api.ResetCommand.ResetType.SOFT)
                .setRef(target)
                .call();
            
            TUIHelper.printSuccess("되돌리기 완료!");
            TUIHelper.printInfo("현재 위치: " + ref.getName());
            
            if (hard) {
                TUIHelper.printWarning("작업 디렉토리가 깨끗하게 정리되었습니다");
            } else {
                TUIHelper.printInfo("변경사항은 스테이징 영역에 남아있습니다");
            }
            
        } catch (Exception e) {
            TUIHelper.printError("되돌리기 실패: " + e.getMessage());
        }
    }
}
