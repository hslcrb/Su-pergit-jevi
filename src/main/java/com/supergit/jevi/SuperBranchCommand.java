package com.supergit.jevi;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.List;

/**
 * 슈퍼 브랜치 - 브랜치 관리를 쉽게
 */
@Command(name = "branch", description = "슈퍼하게 브랜치를 관리합니다")
public class SuperBranchCommand implements Runnable {

    @Parameters(index = "0", description = "브랜치 이름 (생성할 경우)", arity = "0..1")
    private String branchName;

    @Option(names = {"-c", "--create"}, description = "새 브랜치 생성")
    private boolean create = false;

    @Option(names = {"-d", "--delete"}, description = "브랜치 삭제")
    private boolean delete = false;

    @Option(names = {"-l", "--list"}, description = "브랜치 목록 표시")
    private boolean list = false;

    @Override
    public void run() {
        try {
            File currentDir = new File(System.getProperty("user.dir"));
            Git git = Git.open(currentDir);

            if (list || (branchName == null && !create && !delete)) {
                // 브랜치 목록 표시
                System.out.println("🐦 브랜치 목록:");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                List<Ref> branches = git.branchList().call();
                String currentBranch = git.getRepository().getBranch();
                
                for (Ref ref : branches) {
                    String name = ref.getName().replace("refs/heads/", "");
                    if (name.equals(currentBranch)) {
                        System.out.println("  * 🌟 " + name + " (현재)");
                    } else {
                        System.out.println("    🌿 " + name);
                    }
                }
            } else if (create && branchName != null) {
                // 새 브랜치 생성
                git.branchCreate()
                   .setName(branchName)
                   .call();
                System.out.println("✨ 새 브랜치 생성됨: " + branchName);
            } else if (delete && branchName != null) {
                // 브랜치 삭제
                git.branchDelete()
                   .setBranchNames(branchName)
                   .call();
                System.out.println("🗑️  브랜치 삭제됨: " + branchName);
            }

            git.close();

        } catch (Exception e) {
            System.err.println("❌ 브랜치 작업 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
