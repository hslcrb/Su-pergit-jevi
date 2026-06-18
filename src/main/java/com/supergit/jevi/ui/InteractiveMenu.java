package com.supergit.jevi.ui;

import com.supergit.jevi.commands.*;
import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.util.Scanner;

/**
 * 대화형 메뉴 시스템
 */
public class InteractiveMenu {
    
    private final Scanner scanner;
    private boolean running;
    private Git git;
    
    public InteractiveMenu() {
        this.scanner = new Scanner(System.in);
        this.running = true;
    }
    
    public void start() {
        try {
            // Git 저장소 초기화
            File currentDir = new File(System.getProperty("user.dir"));
            this.git = Git.open(currentDir);
            
            TUIHelper.printHeader("SuperGit-Jevi", "제비처럼 빠른 Git 도구에 오신 것을 환영합니다!");
            
            while (running) {
                showMenu();
                handleInput();
            }
            
        } catch (Exception e) {
            TUIHelper.printError("Git 저장소를 찾을 수 없습니다: " + e.getMessage());
            TUIHelper.printInfo("Git 저장소에서 실행해주세요 (git init 또는 git clone)");
        } finally {
            if (git != null) {
                git.close();
            }
            scanner.close();
        }
    }
    
    private void showMenu() {
        System.out.println();
        TUIHelper.printDivider();
        System.out.println("주 메뉴 - 원하는 작업을 선택하세요:");
        TUIHelper.printDivider();
        System.out.println("  [1] 상태 확인 (Status)");
        System.out.println("  [2] 변경사항 커밋 (Commit)");
        System.out.println("  [3] 원격 저장소로 Push (안전 모드)");
        System.out.println("  [4] 브랜치 관리 (Branch)");
        System.out.println("  [5] 커밋 히스토리 (History)");
        System.out.println("  [6] 원격에서 Pull");
        System.out.println("  [7] 도움말 (Help)");
        System.out.println("  [0] 종료 (Exit)");
        TUIHelper.printDivider();
        System.out.print("선택 > ");
    }
    
    private void handleInput() {
        String input = scanner.nextLine().trim();
        
        switch (input) {
            case "1" -> showStatus();
            case "2" -> doCommit();
            case "3" -> doPush();
            case "4" -> manageBranches();
            case "5" -> showHistory();
            case "6" -> doPull();
            case "7" -> showHelp();
            case "0" -> {
                TUIHelper.printSuccess("SuperGit-Jevi를 종료합니다. 안녕히 가세요!");
                running = false;
            }
            default -> TUIHelper.printWarning("잘못된 입력입니다. 0-7 사이의 숫자를 입력하세요.");
        }
    }
    
    private void showStatus() {
        StatusCommand cmd = new StatusCommand(git);
        cmd.execute();
        pressEnterToContinue();
    }
    
    private void doCommit() {
        System.out.print("모든 변경사항을 포함하시겠습니까? (y/n): ");
        boolean addAll = scanner.nextLine().trim().equalsIgnoreCase("y");
        
        System.out.print("커밋 메시지를 입력하세요: ");
        String message = scanner.nextLine().trim();
        
        if (message.isEmpty()) {
            TUIHelper.printWarning("커밋 메시지가 비어있습니다. 취소합니다.");
            return;
        }
        
        CommitCommand cmd = new CommitCommand(git, message, addAll);
        cmd.execute();
        pressEnterToContinue();
    }
    
    private void doPush() {
        TUIHelper.printWarning("Push를 시작합니다. 자동으로 fetch와 pull을 먼저 수행합니다.");
        System.out.print("계속하시겠습니까? (y/n): ");
        
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            TUIHelper.printInfo("Push를 취소했습니다.");
            return;
        }
        
        PushCommand cmd = new PushCommand(git, false);
        cmd.execute();
        pressEnterToContinue();
    }
    
    private void manageBranches() {
        System.out.println();
        System.out.println("브랜치 관리 메뉴:");
        System.out.println("  [1] 브랜치 목록 보기");
        System.out.println("  [2] 새 브랜치 만들기");
        System.out.println("  [3] 브랜치 삭제하기");
        System.out.println("  [4] 브랜치 전환하기");
        System.out.println("  [0] 뒤로 가기");
        System.out.print("선택 > ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1" -> {
                BranchCommand cmd = new BranchCommand(git);
                cmd.listBranches();
            }
            case "2" -> {
                System.out.print("새 브랜치 이름: ");
                String name = scanner.nextLine().trim();
                if (!name.isEmpty()) {
                    BranchCommand cmd = new BranchCommand(git);
                    cmd.createBranch(name);
                }
            }
            case "3" -> {
                System.out.print("삭제할 브랜치 이름: ");
                String name = scanner.nextLine().trim();
                if (!name.isEmpty()) {
                    BranchCommand cmd = new BranchCommand(git);
                    cmd.deleteBranch(name);
                }
            }
            case "4" -> {
                System.out.print("전환할 브랜치 이름: ");
                String name = scanner.nextLine().trim();
                if (!name.isEmpty()) {
                    BranchCommand cmd = new BranchCommand(git);
                    cmd.checkoutBranch(name);
                }
            }
            case "0" -> {
                return;
            }
            default -> TUIHelper.printWarning("잘못된 선택입니다.");
        }
        pressEnterToContinue();
    }
    
    private void showHistory() {
        System.out.print("표시할 커밋 개수 (기본 10개, Enter로 건너뛰기): ");
        String input = scanner.nextLine().trim();
        
        int count = 10;
        if (!input.isEmpty()) {
            try {
                count = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                TUIHelper.printWarning("숫자가 아닙니다. 기본값 10을 사용합니다.");
            }
        }
        
        HistoryCommand cmd = new HistoryCommand(git, count);
        cmd.execute();
        pressEnterToContinue();
    }
    
    private void doPull() {
        TUIHelper.printInfo("원격 저장소에서 최신 변경사항을 가져옵니다...");
        PullCommand cmd = new PullCommand(git);
        cmd.execute();
        pressEnterToContinue();
    }
    
    private void showHelp() {
        TUIHelper.printHeader("도움말", "SuperGit-Jevi 사용 가이드");
        
        System.out.println("SuperGit-Jevi는 Git을 더 쉽고 안전하게 사용할 수 있게 해주는 도구입니다.");
        System.out.println();
        
        System.out.println("[주요 기능]");
        System.out.println("1. 상태 확인: 현재 저장소의 변경사항을 확인합니다");
        System.out.println("2. 커밋: 변경사항을 저장합니다");
        System.out.println("3. Push: 원격 저장소로 업로드합니다 (자동으로 충돌 방지)");
        System.out.println("4. 브랜치 관리: 브랜치를 만들고 전환하고 삭제합니다");
        System.out.println("5. 히스토리: 과거 커밋 기록을 확인합니다");
        System.out.println("6. Pull: 원격 저장소의 최신 변경사항을 받아옵니다");
        System.out.println();
        
        System.out.println("[안전 기능]");
        System.out.println("- Push 전 자동으로 fetch와 pull을 수행합니다");
        System.out.println("- 충돌이 있으면 Push를 차단합니다");
        System.out.println("- 브랜치 삭제 시 확인을 요청합니다");
        System.out.println();
        
        System.out.println("[초보자 팁]");
        System.out.println("- 작업 순서: 상태 확인 -> 커밋 -> Push");
        System.out.println("- 커밋은 자주 하는 것이 좋습니다 (작은 단위로)");
        System.out.println("- Push 전에는 항상 최신 상태를 확인합니다");
        System.out.println("- 브랜치를 사용하면 안전하게 실험할 수 있습니다");
        
        pressEnterToContinue();
    }
    
    private void pressEnterToContinue() {
        System.out.println();
        System.out.print("계속하려면 Enter를 누르세요...");
        scanner.nextLine();
    }
}
