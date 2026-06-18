package com.supergit.jevi.ui;

import com.supergit.jevi.commands.*;
import com.supergit.jevi.core.ConflictResolver;
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
        System.out.println("  [1] 상태 확인 (Status) (for human)");
        System.out.println("  [2] 변경사항 커밋 (Commit) (for human)");
        System.out.println("  [3] 원격 저장소로 Push (안전 모드) (for human)");
        System.out.println("  [4] 브랜치 관리 (Branch) (for human)");
        System.out.println("  [5] 커밋 히스토리 (History) (for human)");
        System.out.println("  [6] 원격에서 Pull (for human)");
        System.out.println();
        System.out.println("  === 고급 기능 ===");
        System.out.println("  [7] 커밋 되돌리기 (Reset) (for human)");
        System.out.println("  [8] 충돌 해결 (Conflict Resolver) (for human)");
        System.out.println("  [9] 저장소 초기화/복제 (Init/Clone) (for human)");
        System.out.println("  [10] 커밋 탐색기 (Explore) (for human)");
        System.out.println("  [11] 스마트 검색 (Search) (for human)");
        System.out.println("  [12] 스마트 비교 (Diff) (for human)");
        System.out.println("  [13] 임시 저장 (Stash) (for human)");
        System.out.println();
        System.out.println("  === AI Agent 기능 ===");
        System.out.println("  [14] 스냅샷 (Snapshot) (for AI) - 통합 저장소 뷰");
        System.out.println();
        System.out.println("  [h] 도움말 (Help) (for human, AI)");
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
            case "7" -> doReset();
            case "8" -> resolveConflicts();
            case "9" -> initOrClone();
            case "10" -> exploreCommits();
            case "11" -> smartSearch();
            case "12" -> smartDiff();
            case "13" -> smartStash();
            case "14" -> generateSnapshot();
            case "h", "H" -> showHelp();
            case "0" -> {
                TUIHelper.printSuccess("SuperGit-Jevi를 종료합니다. 안녕히 가세요!");
                running = false;
            }
            default -> TUIHelper.printWarning("잘못된 입력입니다. 메뉴에서 선택하세요.");
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
        System.out.println("7. Reset: 이전 커밋으로 되돌립니다");
        System.out.println("8. 충돌 해결: 병합 충돌을 자동으로 해결합니다");
        System.out.println("9. Init/Clone: 새 저장소를 만들거나 복제합니다");
        System.out.println("10. 커밋 탐색기: 과거 커밋을 깊이 탐색합니다");
        System.out.println("11. 스마트 검색: 다양한 방식으로 검색합니다");
        System.out.println("12. 스마트 비교: 변경사항을 비교합니다");
        System.out.println("13. 임시 저장: Stash로 작업을 보관합니다");
        System.out.println("14. 스냅샷 (AI): AI Agent를 위한 통합 저장소 뷰");
        System.out.println();
        
        System.out.println("[AI Agent를 위한 Snapshot 기능]");
        System.out.println("- 저장소의 모든 히스토리와 현재 상태를 통합");
        System.out.println("- JSON 또는 TEXT 형식으로 출력");
        System.out.println("- 파일별 전체 진화 과정 추적");
        System.out.println("- 삭제된 파일 포함 가상 파일 시스템");
        System.out.println("- AI가 저장소를 완전히 이해할 수 있는 컨텍스트 제공");
        System.out.println();
        
        System.out.println("[안전 기능]");
        System.out.println("- Push 전 자동으로 fetch와 pull을 수행합니다");
        System.out.println("- 충돌이 있으면 Push를 차단합니다");
        System.out.println("- 자동 충돌 해결 기능으로 병합 충돌을 해결합니다");
        System.out.println("- 브랜치 삭제 시 확인을 요청합니다");
        System.out.println();
        
        System.out.println("[초보자 팁]");
        System.out.println("- 작업 순서: 상태 확인 -> 커밋 -> Push");
        System.out.println("- 커밋은 자주 하는 것이 좋습니다 (작은 단위로)");
        System.out.println("- Push 전에는 항상 최신 상태를 확인합니다");
        System.out.println("- 브랜치를 사용하면 안전하게 실험할 수 있습니다");
        System.out.println("- Reset은 신중하게 사용하세요 (특히 HARD 모드)");
        
        pressEnterToContinue();
    }
    
    private void doReset() {
        System.out.print("되돌릴 커밋 개수 (1-10): ");
        String input = scanner.nextLine().trim();
        
        int count = 1;
        try {
            count = Integer.parseInt(input);
            if (count < 1 || count > 10) {
                TUIHelper.printWarning("1-10 사이의 숫자를 입력하세요.");
                return;
            }
        } catch (NumberFormatException e) {
            TUIHelper.printWarning("올바른 숫자를 입력하세요.");
            return;
        }
        
        System.out.print("HARD 모드로 되돌리시겠습니까? (모든 변경사항 삭제) (y/n): ");
        boolean hard = scanner.nextLine().trim().equalsIgnoreCase("y");
        
        if (hard) {
            TUIHelper.printWarning("경고: HARD 모드는 모든 변경사항을 삭제합니다!");
            System.out.print("정말로 진행하시겠습니까? (yes 입력): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                TUIHelper.printInfo("취소되었습니다.");
                return;
            }
        }
        
        ResetCommand cmd = new ResetCommand(git, count, hard);
        cmd.execute();
        pressEnterToContinue();
    }
    
    private void resolveConflicts() {
        ConflictResolver resolver = new ConflictResolver(git);
        
        if (!resolver.hasConflicts()) {
            TUIHelper.printInfo("현재 충돌이 없습니다.");
            pressEnterToContinue();
            return;
        }
        
        TUIHelper.printHeader("충돌 해결", "자동으로 병합 충돌을 해결합니다");
        
        System.out.println("충돌 파일:");
        for (String file : resolver.getConflictingFiles()) {
            System.out.println("  - " + file);
        }
        System.out.println();
        
        System.out.println("해결 전략 선택:");
        System.out.println("  [1] 로컬 변경사항 우선 (OURS)");
        System.out.println("  [2] 원격 변경사항 우선 (THEIRS)");
        System.out.println("  [3] 둘 다 병합 (더미 공백 추가)");
        System.out.println("  [0] 취소");
        System.out.print("선택 > ");
        
        String choice = scanner.nextLine().trim();
        
        ConflictResolver.ResolveStrategy strategy = switch (choice) {
            case "1" -> ConflictResolver.ResolveStrategy.ACCEPT_OURS;
            case "2" -> ConflictResolver.ResolveStrategy.ACCEPT_THEIRS;
            case "3" -> ConflictResolver.ResolveStrategy.APPEND_BOTH;
            default -> null;
        };
        
        if (strategy == null) {
            TUIHelper.printInfo("취소되었습니다.");
        } else {
            boolean success = resolver.autoResolve(strategy);
            if (success) {
                TUIHelper.printSuccess("충돌이 해결되었습니다! 이제 커밋할 수 있습니다.");
            } else {
                TUIHelper.printWarning("일부 충돌은 수동으로 해결해야 합니다.");
            }
        }
        
        pressEnterToContinue();
    }
    
    private void initOrClone() {
        System.out.println();
        System.out.println("저장소 초기화/복제:");
        System.out.println("  [1] 새 저장소 초기화 (Init)");
        System.out.println("  [2] 원격 저장소 복제 (Clone)");
        System.out.println("  [0] 뒤로 가기");
        System.out.print("선택 > ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1" -> {
                System.out.print("초기화할 경로 (Enter = 현재 디렉토리): ");
                String path = scanner.nextLine().trim();
                if (path.isEmpty()) {
                    path = System.getProperty("user.dir");
                }
                InitCommand cmd = new InitCommand(path);
                cmd.execute();
            }
            case "2" -> {
                System.out.print("원격 저장소 URL: ");
                String url = scanner.nextLine().trim();
                System.out.print("복제할 경로: ");
                String path = scanner.nextLine().trim();
                
                if (!url.isEmpty() && !path.isEmpty()) {
                    CloneCommand cmd = new CloneCommand(url, path);
                    cmd.execute();
                } else {
                    TUIHelper.printWarning("URL과 경로를 모두 입력해야 합니다.");
                }
            }
            case "0" -> {
                return;
            }
            default -> TUIHelper.printWarning("잘못된 선택입니다.");
        }
        pressEnterToContinue();
    }
    
    private void pressEnterToContinue() {
        System.out.println();
        System.out.print("계속하려면 Enter를 누르세요...");
        scanner.nextLine();
    }
    
    private void exploreCommits() {
        CommitExplorerCommand cmd = new CommitExplorerCommand(git, scanner);
        cmd.execute();
        pressEnterToContinue();
    }
    
    private void smartSearch() {
        SmartSearchCommand cmd = new SmartSearchCommand(git, scanner);
        cmd.execute();
        pressEnterToContinue();
    }
    
    private void smartDiff() {
        SmartDiffCommand cmd = new SmartDiffCommand(git, scanner);
        cmd.execute();
        pressEnterToContinue();
    }
    
    private void smartStash() {
        SmartStashCommand cmd = new SmartStashCommand(git, scanner);
        cmd.execute();
        pressEnterToContinue();
    }
    
    private void generateSnapshot() {
        SnapshotCommand cmd = new SnapshotCommand(git, scanner);
        cmd.execute();
        pressEnterToContinue();
    }
}
