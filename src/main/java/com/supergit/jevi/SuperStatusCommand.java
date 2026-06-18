package com.supergit.jevi;

import com.supergit.jevi.core.TUIHelper;
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

            TUIHelper.printHeader("SuperGit-Jevi 상태", "현재 저장소 상태를 확인합니다");

            Status status = git.status().call();

            // 브랜치 정보
            String branch = git.getRepository().getBranch();
            TUIHelper.printInfo("📍 현재 브랜치: " + branch);
            TUIHelper.printDivider();

            int totalChanges = 0;

            // 수정된 파일
            if (!status.getModified().isEmpty()) {
                System.out.println("\n📝 수정된 파일:");
                status.getModified().forEach(file -> TUIHelper.printFileStatus("MODIFIED", file));
                totalChanges += status.getModified().size();
            }

            // 추가된 파일
            if (!status.getAdded().isEmpty()) {
                System.out.println("\n➕ 스테이징된 파일:");
                status.getAdded().forEach(file -> TUIHelper.printFileStatus("ADDED", file));
                totalChanges += status.getAdded().size();
            }

            // 삭제된 파일
            if (!status.getRemoved().isEmpty()) {
                System.out.println("\n🗑️  삭제된 파일:");
                status.getRemoved().forEach(file -> TUIHelper.printFileStatus("DELETED", file));
                totalChanges += status.getRemoved().size();
            }

            // 추적되지 않는 파일
            if (!status.getUntracked().isEmpty()) {
                System.out.println("\n❓ 추적되지 않는 파일:");
                status.getUntracked().forEach(file -> TUIHelper.printFileStatus("UNTRACKED", file));
                totalChanges += status.getUntracked().size();
            }

            // 충돌 파일
            if (!status.getConflicting().isEmpty()) {
                System.out.println("\n⚔️  충돌 파일:");
                status.getConflicting().forEach(file -> TUIHelper.printFileStatus("CONFLICTING", file));
                totalChanges += status.getConflicting().size();
            }

            System.out.println();
            TUIHelper.printDivider();

            // 요약
            if (status.isClean()) {
                TUIHelper.printBox("✨ 작업 트리가 깨끗합니다!", TUIHelper.BoxStyle.SUCCESS);
            } else {
                TUIHelper.printInfo("총 " + totalChanges + "개의 변경사항이 있습니다.");
            }

            git.close();

        } catch (Exception e) {
            TUIHelper.printError("❌ 상태 확인 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
