package com.supergit.jevi;

import com.supergit.jevi.core.TUIHelper;
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
                TUIHelper.printHeader("브랜치 목록", "현재 저장소의 모든 브랜치");
                
                List<Ref> branches = git.branchList().call();
                String currentBranch = git.getRepository().getBranch();
                
                for (Ref ref : branches) {
                    String name = ref.getName().replace("refs/heads/", "");
                    if (name.equals(currentBranch)) {
                        TUIHelper.printSuccess("* " + name + " (현재)");
                    } else {
                        TUIHelper.printInfo("  " + name);
                    }
                }
                
                TUIHelper.printDivider();
                TUIHelper.printInfo("총 " + branches.size() + "개의 브랜치");
                
            } else if (create && branchName != null) {
                TUIHelper.printHeader("브랜치 생성", "새로운 브랜치를 만듭니다");
                
                // 새 브랜치 생성
                git.branchCreate()
                   .setName(branchName)
                   .call();
                   
                TUIHelper.printSuccess("✨ 새 브랜치 생성됨: " + branchName);
                TUIHelper.printBox("브랜치 '" + branchName + "'이 생성되었습니다!", TUIHelper.BoxStyle.SUCCESS);
                
            } else if (delete && branchName != null) {
                TUIHelper.printHeader("브랜치 삭제", "브랜치를 삭제합니다");
                
                if (TUIHelper.confirm("정말로 브랜치 '" + branchName + "'을(를) 삭제하시겠습니까?")) {
                    // 브랜치 삭제
                    git.branchDelete()
                       .setBranchNames(branchName)
                       .call();
                    TUIHelper.printSuccess("🗑️  브랜치 삭제됨: " + branchName);
                } else {
                    TUIHelper.printInfo("삭제가 취소되었습니다.");
                }
            }

            git.close();

        } catch (Exception e) {
            TUIHelper.printError("❌ 브랜치 작업 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
