package com.supergit.jevi;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
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

            // 상태 확인
            Status status = git.status().call();
            
            System.out.println("🐦 제비처럼 빠르게 커밋을 준비합니다...");
            
            if (addAll) {
                // 모든 변경사항 추가
                git.add().addFilepattern(".").call();
                System.out.println("✅ 모든 변경사항이 스테이징되었습니다.");
            }

            // 커밋 메시지 처리
            String finalMessage = message;
            if (quick) {
                finalMessage = message + " [" + java.time.LocalDateTime.now() + "]";
            }

            // 커밋 실행
            git.commit()
               .setMessage(finalMessage)
               .call();

            System.out.println("✨ 슈퍼 커밋 완료!");
            System.out.println("💬 메시지: " + finalMessage);
            
            git.close();

        } catch (Exception e) {
            System.err.println("❌ 커밋 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
