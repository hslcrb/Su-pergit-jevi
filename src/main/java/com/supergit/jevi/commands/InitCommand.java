package com.supergit.jevi.commands;

import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;

import java.io.File;

/**
 * Init 명령 - 새로운 Git 저장소 초기화
 */
public class InitCommand {
    private final String path;
    
    public InitCommand(String path) {
        this.path = path;
    }
    
    public void execute() {
        try {
            TUIHelper.printHeader("Git 저장소 초기화", "새로운 저장소를 생성합니다");
            
            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
                TUIHelper.printInfo("디렉토리 생성: " + path);
            }
            
            TUIHelper.printStep("Git 저장소 초기화 중...");
            
            Git git = Git.init()
                .setDirectory(directory)
                .call();
            
            TUIHelper.printSuccess("Git 저장소가 초기화되었습니다!");
            TUIHelper.printInfo("위치: " + directory.getAbsolutePath());
            
            git.close();
            
        } catch (Exception e) {
            TUIHelper.printError("초기화 실패: " + e.getMessage());
        }
    }
}
