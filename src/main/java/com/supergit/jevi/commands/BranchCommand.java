package com.supergit.jevi.commands;

import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;

import java.util.List;

/**
 * 브랜치 명령 (최적화됨)
 */
public class BranchCommand {
    private final Git git;
    
    public BranchCommand(Git git) {
        this.git = git;
    }
    
    public void listBranches() {
        try {
            TUIHelper.printHeader("브랜치 목록", null);
            
            List<Ref> branches = git.branchList().call();
            String currentBranch = git.getRepository().getBranch();
            
            for (Ref ref : branches) {
                String name = ref.getName().replace("refs/heads/", "");
                if (name.equals(currentBranch)) {
                    System.out.println("  * " + name + " (현재)");
                } else {
                    System.out.println("    " + name);
                }
            }
            
            System.out.println();
            TUIHelper.printInfo("총 " + branches.size() + "개의 브랜치");
            
        } catch (Exception e) {
            TUIHelper.printError("브랜치 목록 조회 실패: " + e.getMessage());
        }
    }
    
    public void createBranch(String name) {
        try {
            git.branchCreate().setName(name).call();
            TUIHelper.printSuccess("브랜치 생성 완료: " + name);
        } catch (Exception e) {
            TUIHelper.printError("브랜치 생성 실패: " + e.getMessage());
        }
    }
    
    public void deleteBranch(String name) {
        try {
            git.branchDelete().setBranchNames(name).call();
            TUIHelper.printSuccess("브랜치 삭제 완료: " + name);
        } catch (Exception e) {
            TUIHelper.printError("브랜치 삭제 실패: " + e.getMessage());
        }
    }
    
    public void checkoutBranch(String name) {
        try {
            git.checkout().setName(name).call();
            TUIHelper.printSuccess("브랜치 전환 완료: " + name);
        } catch (Exception e) {
            TUIHelper.printError("브랜치 전환 실패: " + e.getMessage());
        }
    }
}
