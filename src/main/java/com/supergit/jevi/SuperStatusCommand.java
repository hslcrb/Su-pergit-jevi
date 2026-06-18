package com.supergit.jevi;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;

/**
 * 슈퍼 상태 - 예쁘고 직관적으로 Git 상태 표시
 */
@Command(name = "status", description = "슈퍼하게 저장소 상태를 표시합니다")
public class SuperStatusCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "상세 정보 표시")
    private boolean verbose = false;

    @Override
    public void run() {
        try {
            File currentDir = new File(System.getProperty("user.dir"));
            Git git = Git.open(currentDir);

            System.out.println("🐦 SuperGit-Jevi 상태 확인");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            Status status = git.status().call();

            // 브랜치 정보
            String branch = git.getRepository().getBranch();
            System.out.println("📍 현재 브랜치: " + branch);
            System.out.println();

            // 수정된 파일
            if (!status.getModified().isEmpty()) {
                System.out.println("📝 수정된 파일:");
                status.getModified().forEach(file -> System.out.println("   • " + file));
                System.out.println();
            }

            // 추가된 파일
            if (!status.getAdded().isEmpty()) {
                System.out.println("➕ 추가된 파일:");
                status.getAdded().forEach(file -> System.out.println("   • " + file));
                System.out.println();
            }

            // 삭제된 파일
            if (!status.getRemoved().isEmpty()) {
                System.out.println("🗑️  삭제된 파일:");
                status.getRemoved().forEach(file -> System.out.println("   • " + file));
                System.out.println();
            }

            // 추적되지 않는 파일
            if (!status.getUntracked().isEmpty()) {
                System.out.println("❓ 추적되지 않는 파일:");
                status.getUntracked().forEach(file -> System.out.println("   • " + file));
                System.out.println();
            }

            // 클린 상태
            if (status.isClean()) {
                System.out.println("✨ 작업 트리가 깨끗합니다!");
            }

            git.close();

        } catch (Exception e) {
            System.err.println("❌ 상태 확인 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
