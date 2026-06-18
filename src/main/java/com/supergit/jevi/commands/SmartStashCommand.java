package com.supergit.jevi.commands;

import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Collection;
import java.util.Scanner;

/**
 * 스마트 임시 저장 (Stash) - 작업을 안전하게 임시 보관
 */
public class SmartStashCommand {
    private final Git git;
    private final Scanner scanner;
    
    public SmartStashCommand(Git git, Scanner scanner) {
        this.git = git;
        this.scanner = scanner;
    }
    
    public void execute() {
        try {
            TUIHelper.printHeader("스마트 임시 저장", "작업을 안전하게 보관하고 복원합니다");
            
            System.out.println("[임시 저장 메뉴]");
            System.out.println("  [1] 현재 작업 임시 저장 (Stash Save)");
            System.out.println("  [2] 임시 저장 목록 보기");
            System.out.println("  [3] 임시 저장 복원 (Stash Pop)");
            System.out.println("  [4] 임시 저장 적용 (Stash Apply)");
            System.out.println("  [5] 임시 저장 삭제");
            System.out.println("  [0] 뒤로 가기");
            System.out.print("선택 > ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1 -> saveStash();
                case 2 -> listStashes();
                case 3 -> popStash();
                case 4 -> applyStash();
                case 5 -> dropStash();
                case 0 -> TUIHelper.printInfo("취소되었습니다.");
                default -> TUIHelper.printWarning("잘못된 선택입니다.");
            }
            
        } catch (Exception e) {
            TUIHelper.printError("임시 저장 실패: " + e.getMessage());
        }
    }
    
    private void saveStash() throws Exception {
        System.out.print("임시 저장 메시지 (선택사항): ");
        String message = scanner.nextLine().trim();
        
        TUIHelper.printStep("작업 내용을 임시 저장 중...");
        
        RevCommit stash;
        if (message.isEmpty()) {
            stash = git.stashCreate().call();
        } else {
            stash = git.stashCreate().setWorkingDirectoryMessage(message).call();
        }
        
        if (stash != null) {
            TUIHelper.printSuccess("임시 저장 완료!");
            TUIHelper.printInfo("작업 디렉토리가 깨끗해졌습니다");
            TUIHelper.printInfo("복원하려면 'Stash Pop' 또는 'Stash Apply'를 사용하세요");
        } else {
            TUIHelper.printWarning("저장할 변경사항이 없습니다.");
        }
    }
    
    private void listStashes() throws Exception {
        Collection<RevCommit> stashes = git.stashList().call();
        
        if (stashes.isEmpty()) {
            TUIHelper.printInfo("임시 저장된 작업이 없습니다.");
            return;
        }
        
        System.out.println();
        TUIHelper.printSuccess(stashes.size() + "개의 임시 저장:");
        System.out.println();
        
        int index = 0;
        for (RevCommit stash : stashes) {
            System.out.printf("[%d] stash@{%d}: %s%n",
                index,
                index,
                stash.getShortMessage()
            );
            index++;
        }
    }
    
    private void popStash() throws Exception {
        Collection<RevCommit> stashes = git.stashList().call();
        
        if (stashes.isEmpty()) {
            TUIHelper.printInfo("임시 저장된 작업이 없습니다.");
            return;
        }
        
        listStashes();
        
        System.out.print("\n복원할 stash 번호 (기본 0): ");
        String input = scanner.nextLine().trim();
        int index = input.isEmpty() ? 0 : Integer.parseInt(input);
        
        TUIHelper.printStep("Stash 복원 중... (복원 후 삭제됨)");
        
        git.stashApply().setStashRef("stash@{" + index + "}").call();
        git.stashDrop().setStashRef(index).call();
        
        TUIHelper.printSuccess("복원 완료! 해당 stash는 삭제되었습니다.");
    }
    
    private void applyStash() throws Exception {
        Collection<RevCommit> stashes = git.stashList().call();
        
        if (stashes.isEmpty()) {
            TUIHelper.printInfo("임시 저장된 작업이 없습니다.");
            return;
        }
        
        listStashes();
        
        System.out.print("\n적용할 stash 번호 (기본 0): ");
        String input = scanner.nextLine().trim();
        int index = input.isEmpty() ? 0 : Integer.parseInt(input);
        
        TUIHelper.printStep("Stash 적용 중... (stash는 유지됨)");
        
        git.stashApply().setStashRef("stash@{" + index + "}").call();
        
        TUIHelper.printSuccess("적용 완료! Stash는 보존되었습니다.");
    }
    
    private void dropStash() throws Exception {
        Collection<RevCommit> stashes = git.stashList().call();
        
        if (stashes.isEmpty()) {
            TUIHelper.printInfo("임시 저장된 작업이 없습니다.");
            return;
        }
        
        listStashes();
        
        System.out.print("\n삭제할 stash 번호: ");
        int index = getIntInput();
        
        System.out.print("정말로 삭제하시겠습니까? (y/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            TUIHelper.printInfo("취소되었습니다.");
            return;
        }
        
        git.stashDrop().setStashRef(index).call();
        TUIHelper.printSuccess("Stash 삭제 완료!");
    }
    
    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
