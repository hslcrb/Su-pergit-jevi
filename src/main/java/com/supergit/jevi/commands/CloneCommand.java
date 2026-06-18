package com.supergit.jevi.commands;

import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;

import java.io.File;

/**
 * Clone 명령 - 원격 저장소 복제
 */
public class CloneCommand {
    private final String repoUrl;
    private final String targetPath;
    
    public CloneCommand(String repoUrl, String targetPath) {
        this.repoUrl = repoUrl;
        this.targetPath = targetPath;
    }
    
    public void execute() {
        try {
            TUIHelper.printHeader("저장소 복제", "원격 저장소를 로컬로 복제합니다");
            
            TUIHelper.printInfo("원격 저장소: " + repoUrl);
            TUIHelper.printInfo("대상 경로: " + targetPath);
            TUIHelper.printStep("복제 중... (시간이 걸릴 수 있습니다)");
            
            Git git = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(new File(targetPath))
                .setProgressMonitor(new org.eclipse.jgit.lib.TextProgressMonitor(
                    new java.io.PrintWriter(System.out)))
                .call();
            
            TUIHelper.printSuccess("복제 완료!");
            TUIHelper.printInfo("저장소 위치: " + new File(targetPath).getAbsolutePath());
            
            git.close();
            
        } catch (Exception e) {
            TUIHelper.printError("복제 실패: " + e.getMessage());
        }
    }
}
