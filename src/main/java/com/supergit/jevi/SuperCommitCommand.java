package com.supergit.jevi;

import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.revwalk.RevCommit;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;

/**
 * 슈퍼 커밋 - 자동으로 변경사항을 감지하고 스마트하게 커밋
 */
@Command(name = "commit", description = "슈퍼하게 변경사항을 커밋합니다")
public class SuperCommitCommand implements Runnable {

    @Parameters(index = "0", description = "커밋 메시지")
    private String message;

    @Option(names = {"-a", "--all"}, description = "모든 변경사항 포함")
    private boolean addAll = false;

    @Option(names = {"-q", "--quick"}, description = "빠른 커밋 (타임스탬프 자동 추가)")
    private boolean quick = false;

    @Override
    public void run() {
        try {
            File currentDir = new File(System.getProperty("user.dir"));
            Git git = Git.open(currentDir);

            TUIHelper.printHeader("SuperGit-Jevi Commit", "제비처럼 빠르게 커밋합니다");

            // 상태 확인
            Status status = git.status().call();
            
            if (status.isClean() && !addAll) {
                TUIHelper.printBox("커밋할 변경사항이 없습니다", TUIHelper.BoxStyle.INFO);
                git.close();
                return;
            }
            
            if (addAll) {
                TUIHelper.printStep("모든 변경사항을 스테이징 중...");
                
                // 수정된 파일들 표시
                int fileCount = status.getModified().size() + 
                               status.getUntracked().size() + 
                               status.getRemoved().size();
                
                if (fileCount > 0) {
                    git.add().addFilepattern(".").call();
                    TUIHelper.printSuccess("✅ " + fileCount + "개 파일이 스테이징되었습니다");
                }
            }

            // 커밋 메시지 처리
            String finalMessage = message;
            if (quick) {
                finalMessage = message + " [" + java.time.LocalDateTime.now() + "]";
            }

            TUIHelper.printDivider();
            TUIHelper.printStep("커밋 생성 중...");

            // 커밋 실행
            RevCommit commit = git.commit()
               .setMessage(finalMessage)
               .call();

            TUIHelper.printSuccess("✨ 슈퍼 커밋 완료!");
            TUIHelper.printInfo("🔑 커밋 해시: " + commit.getName().substring(0, 7));
            TUIHelper.printInfo("💬 메시지: " + finalMessage);
            
            TUIHelper.printBox("커밋이 성공적으로 생성되었습니다! 🎉", TUIHelper.BoxStyle.SUCCESS);
            
            git.close();

        } catch (Exception e) {
            TUIHelper.printError("❌ 커밋 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
