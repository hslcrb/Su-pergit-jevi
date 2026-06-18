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
        
        System.out.println("SuperGit-Jevi는 Git을 더 쉽고 안전하게 사용할 수 있게 해주는 TUI 도구입니다.");
        System.out.println("제비처럼 빠르지만, 안전장치를 통해 실수를 방지합니다.");
        System.out.println();
        
        TUIHelper.printDivider();
        System.out.println("[기본 기능 - Human용]");
        TUIHelper.printDivider();
        System.out.println();
        
        System.out.println("  [1] 상태 확인 (Status)");
        System.out.println("      - 현재 저장소의 변경사항 확인");
        System.out.println("      - 수정/추가/삭제된 파일 분류 표시");
        System.out.println("      - 사용 시점: 작업 시작 전, 커밋 전 항상 확인");
        System.out.println();
        
        System.out.println("  [2] 변경사항 커밋 (Commit)");
        System.out.println("      - 변경사항을 저장소에 기록");
        System.out.println("      - 변경 파일 미리보기 제공");
        System.out.println("      - 사용 시점: 의미있는 작업 단위 완료 시");
        System.out.println();
        
        System.out.println("  [3] 원격 저장소로 Push (안전 모드)");
        System.out.println("      - 단계1: 자동 Fetch (원격 상태 확인)");
        System.out.println("      - 단계2: 자동 Pull (최신 변경사항 받기)");
        System.out.println("      - 단계3: 충돌 검사");
        System.out.println("      - 단계4: 안전한 경우만 Push 실행");
        System.out.println("      - 사용 시점: 로컬 커밋을 팀과 공유할 때");
        System.out.println();
        
        System.out.println("  [4] 브랜치 관리 (Branch)");
        System.out.println("      - 브랜치 목록 보기, 생성, 전환, 삭제");
        System.out.println("      - 안전장치: 삭제 시 확인 요청");
        System.out.println("      - 사용 시점: 새 기능 개발, 실험적 작업");
        System.out.println();
        
        System.out.println("  [5] 커밋 히스토리 (History)");
        System.out.println("      - 과거 커밋 기록 확인");
        System.out.println("      - 해시, 메시지, 작성자, 날짜 표시");
        System.out.println("      - 사용 시점: 과거 작업 추적, 버그 발생 시점 파악");
        System.out.println();
        
        System.out.println("  [6] 원격에서 Pull");
        System.out.println("      - 원격 저장소의 최신 변경사항 가져오기");
        System.out.println("      - 로컬 작업과 병합");
        System.out.println("      - 사용 시점: 작업 시작 전, 팀원 변경사항 반영");
        System.out.println();
        
        pressEnterToContinue();
        System.out.print("\033[H\033[2J"); // 화면 클리어
        System.out.flush();
        
        TUIHelper.printHeader("도움말 (계속)", "고급 기능");
        
        TUIHelper.printDivider();
        System.out.println("[고급 기능]");
        TUIHelper.printDivider();
        System.out.println();
        
        System.out.println("  [7] 커밋 되돌리기 (Reset)");
        System.out.println("      - SOFT: 커밋만 취소, 변경사항은 유지");
        System.out.println("      - HARD: 모든 것 삭제 (주의!)");
        System.out.println("      - 1-10개 커밋 되돌리기 가능");
        System.out.println("      - 사용 시점: 잘못된 커밋 수정, 과거로 복원");
        System.out.println();
        
        System.out.println("  [8] 충돌 해결 (Conflict Resolver)");
        System.out.println("      - OURS: 로컬 변경사항 우선");
        System.out.println("      - THEIRS: 원격 변경사항 우선");
        System.out.println("      - APPEND BOTH: 양쪽 모두 병합 (타임스탬프 추가)");
        System.out.println("      - 사용 시점: Push 차단 시, 병합 충돌 발생 시");
        System.out.println();
        
        System.out.println("  [9] 저장소 초기화/복제 (Init/Clone)");
        System.out.println("      - Init: 새 Git 저장소 초기화");
        System.out.println("      - Clone: 원격 저장소 복제");
        System.out.println("      - 사용 시점: 프로젝트 시작, 협업 저장소 받기");
        System.out.println();
        
        System.out.println("  [10] 커밋 탐색기 (Commit Explorer)");
        System.out.println("      - 커밋 상세 정보 및 변경 파일 목록");
        System.out.println("      - Diff 보기, 특정 파일 내용 확인");
        System.out.println("      - 시간 여행: 과거 커밋으로 체크아웃");
        System.out.println("      - Cherry-pick: 특정 커밋만 가져오기");
        System.out.println("      - 사용 시점: 버그 추적, 과거 코드 분석");
        System.out.println();
        
        System.out.println("  [11] 스마트 검색 (Smart Search)");
        System.out.println("      - 커밋 메시지, 작성자, 날짜로 검색");
        System.out.println("      - 코드 검색: 파일 내용에서 텍스트 찾기 (줄 번호 표시)");
        System.out.println("      - 파일명 검색: 특정 파일의 커밋 이력");
        System.out.println("      - 사용 시점: 특정 변경사항 찾기, 코드 추적");
        System.out.println();
        
        System.out.println("  [12] 스마트 비교 (Smart Diff)");
        System.out.println("      - 작업 디렉토리 vs HEAD");
        System.out.println("      - 두 커밋 간 비교");
        System.out.println("      - 브랜치 간 비교");
        System.out.println("      - 사용 시점: 변경사항 확인, 브랜치 차이 분석");
        System.out.println();
        
        System.out.println("  [13] 임시 저장 (Smart Stash)");
        System.out.println("      - 현재 작업 임시 보관 (메시지 추가 가능)");
        System.out.println("      - Pop: 복원 후 삭제");
        System.out.println("      - Apply: 복원하되 보관 유지");
        System.out.println("      - 사용 시점: 급한 작업 전환, 실험적 작업 보관");
        System.out.println();
        
        pressEnterToContinue();
        System.out.print("\033[H\033[2J"); // 화면 클리어
        System.out.flush();
        
        TUIHelper.printHeader("도움말 (계속)", "AI 기능 및 사용 팁");
        
        TUIHelper.printDivider();
        System.out.println("[AI Agent 전용 기능]");
        TUIHelper.printDivider();
        System.out.println();
        
        System.out.println("  [14] 스냅샷 (Snapshot)");
        System.out.println("      - 저장소의 모든 히스토리와 현재 상태를 통합");
        System.out.println("      - JSON (AI 최적화) 또는 TEXT (Human) 형식");
        System.out.println("      - 파일별 전체 진화 과정 추적 (생성->수정->삭제)");
        System.out.println("      - 삭제된 파일 포함 가상 파일 시스템");
        System.out.println("      - 사용 예시:");
        System.out.println("        * AI 에이전트가 코드베이스 전체 이해");
        System.out.println("        * 자동 문서화, Release Notes 생성");
        System.out.println("        * 버그 패턴 분석 및 리팩토링 제안");
        System.out.println();
        
        TUIHelper.printDivider();
        System.out.println("[안전 시스템]");
        TUIHelper.printDivider();
        System.out.println();
        
        System.out.println("  SuperGit-Jevi의 핵심은 '안전성'입니다:");
        System.out.println();
        System.out.println("  1. Push 전 3단계 검사");
        System.out.println("     Fetch -> Pull -> 충돌 검사 -> Push");
        System.out.println();
        System.out.println("  2. Pull 없이 Push 하는 인재(人災) 시스템 차단");
        System.out.println("     더 이상 '아차!' 하는 실수가 없습니다");
        System.out.println();
        System.out.println("  3. 위험한 작업 시 확인 요청");
        System.out.println("     브랜치 삭제, HARD Reset 등");
        System.out.println();
        System.out.println("  4. 자동 충돌 해결");
        System.out.println("     3가지 전략으로 병합 충돌 자동 처리");
        System.out.println();
        
        TUIHelper.printDivider();
        System.out.println("[초보자를 위한 워크플로우]");
        TUIHelper.printDivider();
        System.out.println();
        
        System.out.println("  일반적인 작업 순서:");
        System.out.println();
        System.out.println("  1. [6] Pull - 작업 시작 전 최신 상태로");
        System.out.println("  2. 코드 수정 작업");
        System.out.println("  3. [1] Status - 변경사항 확인");
        System.out.println("  4. [2] Commit - 의미있는 단위로 커밋");
        System.out.println("  5. [3] Push - 안전하게 공유");
        System.out.println();
        System.out.println("  충돌 발생 시:");
        System.out.println();
        System.out.println("  1. [3] Push 시도 -> 차단됨");
        System.out.println("  2. [8] 충돌 해결 선택");
        System.out.println("  3. 전략 선택 (OURS/THEIRS/APPEND)");
        System.out.println("  4. [2] Commit으로 병합 저장");
        System.out.println("  5. [3] Push 재시도 -> 성공!");
        System.out.println();
        
        TUIHelper.printDivider();
        System.out.println("[빌드 및 실행]");
        TUIHelper.printDivider();
        System.out.println();
        
        System.out.println("  빌드 명령어:");
        System.out.println("    mvn clean package");
        System.out.println();
        System.out.println("  빌드 + 즉시 실행:");
        System.out.println("    mvn clean package && java -jar target/supergit-jevi.jar");
        System.out.println();
        System.out.println("  빠른 빌드 (테스트 생략):");
        System.out.println("    mvn clean package -DskipTests");
        System.out.println();
        System.out.println("  별칭 설정 후:");
        System.out.println("    jevi  # 어디서든 실행");
        System.out.println();
        
        TUIHelper.printDivider();
        System.out.println("[유용한 팁]");
        TUIHelper.printDivider();
        System.out.println();
        
        System.out.println("  커밋 관련:");
        System.out.println("  - 커밋은 자주, 작은 단위로 하세요");
        System.out.println("  - 커밋 메시지는 명확하게 (무엇을, 왜)");
        System.out.println("  - 관련된 변경사항끼리 묶어서 커밋");
        System.out.println();
        
        System.out.println("  브랜치 관련:");
        System.out.println("  - 새 기능은 별도 브랜치에서 작업");
        System.out.println("  - main 브랜치는 항상 안정적으로 유지");
        System.out.println("  - 브랜치 이름은 의미있게 (feature/login, bugfix/issue-123)");
        System.out.println();
        
        System.out.println("  협업 관련:");
        System.out.println("  - 작업 시작 전 항상 Pull");
        System.out.println("  - 퇴근 전 Push (팀과 공유)");
        System.out.println("  - 충돌은 빨리 해결할수록 쉬움");
        System.out.println();
        
        System.out.println("  Reset 사용:");
        System.out.println("  - SOFT: 커밋만 취소, 코드는 남김 (안전)");
        System.out.println("  - HARD: 모든 것 삭제 (신중하게!)");
        System.out.println("  - Reset 후에는 force push 필요할 수 있음 (주의)");
        System.out.println();
        
        TUIHelper.printDivider();
        System.out.println("[문제 해결]");
        TUIHelper.printDivider();
        System.out.println();
        
        System.out.println("  Push가 차단되면?");
        System.out.println("  -> [8] 충돌 해결 메뉴 사용");
        System.out.println();
        
        System.out.println("  잘못 커밋했으면?");
        System.out.println("  -> [7] Reset으로 되돌리기");
        System.out.println();
        
        System.out.println("  과거 코드 확인하려면?");
        System.out.println("  -> [10] 커밋 탐색기의 시간 여행 기능");
        System.out.println();
        
        System.out.println("  특정 변경사항 찾으려면?");
        System.out.println("  -> [11] 스마트 검색");
        System.out.println();
        
        System.out.println("  급하게 다른 작업해야 하면?");
        System.out.println("  -> [13] Stash로 현재 작업 임시 저장");
        System.out.println();
        
        System.out.println();
        System.out.println("  더 자세한 정보는 README.md를 참조하세요!");
        System.out.println("  GitHub: https://github.com/yourusername/supergit-jevi");
        System.out.println();
        
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
