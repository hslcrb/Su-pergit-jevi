package com.supergit.jevi.commands;

import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;

/**
 * 상태 확인 명령 (최적화됨)
 */
public class StatusCommand {
    private final Git git;
    
    public StatusCommand(Git git) {
        this.git = git;
    }
    
    public void execute() {
        try {
            TUIHelper.printHeader("저장소 상태", "현재 작업 디렉토리 상태 확인");

            Status status = git.status().call();
            String branch = git.getRepository().getBranch();
            
            TUIHelper.printInfo("현재 브랜치: " + branch);
            TUIHelper.printDivider();

            int totalChanges = 0;

            // 수정된 파일
            if (!status.getModified().isEmpty()) {
                System.out.println("\n[수정됨]");
                for (String file : status.getModified()) {
                    System.out.println("  M " + file);
                    totalChanges++;
                }
            }

            // 스테이징된 파일
            if (!status.getAdded().isEmpty()) {
                System.out.println("\n[스테이징됨]");
                for (String file : status.getAdded()) {
                    System.out.println("  A " + file);
                    totalChanges++;
                }
            }

            // 삭제된 파일
            if (!status.getRemoved().isEmpty()) {
                System.out.println("\n[삭제됨]");
                for (String file : status.getRemoved()) {
                    System.out.println("  D " + file);
                    totalChanges++;
                }
            }

            // 추적되지 않는 파일
            if (!status.getUntracked().isEmpty()) {
                System.out.println("\n[추적 안됨]");
                for (String file : status.getUntracked()) {
                    System.out.println("  ? " + file);
                    totalChanges++;
                }
            }

            // 충돌 파일
            if (!status.getConflicting().isEmpty()) {
                System.out.println("\n[충돌!]");
                for (String file : status.getConflicting()) {
                    System.out.println("  ! " + file);
                    totalChanges++;
                }
            }

            System.out.println();
            TUIHelper.printDivider();

            if (status.isClean()) {
                TUIHelper.printSuccess("작업 트리가 깨끗합니다. 변경사항이 없습니다.");
            } else {
                TUIHelper.printInfo("총 " + totalChanges + "개의 변경사항");
            }

        } catch (Exception e) {
            TUIHelper.printError("상태 확인 실패: " + e.getMessage());
        }
    }
}
