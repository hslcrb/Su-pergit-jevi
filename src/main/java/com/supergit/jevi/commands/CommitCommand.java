package com.supergit.jevi.commands;

import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * 커밋 명령 (최적화됨)
 */
public class CommitCommand {
    private final Git git;
    private final String message;
    private final boolean addAll;
    
    public CommitCommand(Git git, String message, boolean addAll) {
        this.git = git;
        this.message = message;
        this.addAll = addAll;
    }
    
    public void execute() {
        try {
            TUIHelper.printHeader("커밋 실행", "변경사항을 저장합니다");

            Status status = git.status().call();
            
            if (status.isClean() && !addAll) {
                TUIHelper.printWarning("커밋할 변경사항이 없습니다.");
                return;
            }
            
            if (addAll) {
                TUIHelper.printStep("모든 변경사항을 스테이징 중...");
                
                int fileCount = status.getModified().size() + 
                               status.getUntracked().size() + 
                               status.getRemoved().size();
                
                if (fileCount > 0) {
                    git.add().addFilepattern(".").call();
                    TUIHelper.printSuccess(fileCount + "개 파일 스테이징 완료");
                }
            }

            TUIHelper.printStep("커밋 생성 중...");

            RevCommit commit = git.commit()
               .setMessage(message)
               .call();

            TUIHelper.printSuccess("커밋 완료!");
            TUIHelper.printInfo("커밋 해시: " + commit.getName().substring(0, 7));
            TUIHelper.printInfo("메시지: " + message);

        } catch (Exception e) {
            TUIHelper.printError("커밋 실패: " + e.getMessage());
        }
    }
}
