package com.supergit.jevi.commands;

import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * 커밋 탐색기 - 커밋의 내용을 상세하게 탐색
 * 혁신적인 UX: 커밋 선택 -> 파일 목록 -> 파일 내용 보기 -> Diff 보기
 */
public class CommitExplorerCommand {
    private final Git git;
    private final Scanner scanner;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public CommitExplorerCommand(Git git, Scanner scanner) {
        this.git = git;
        this.scanner = scanner;
    }
    
    public void execute() {
        try {
            TUIHelper.printHeader("커밋 탐색기", "커밋 내용을 깊이 있게 탐색합니다");
            
            // 1단계: 최근 커밋 목록 표시
            List<RevCommit> commits = showCommitList();
            if (commits.isEmpty()) return;
            
            // 2단계: 커밋 선택
            System.out.print("\n탐색할 커밋 번호를 입력하세요 (0=취소): ");
            int choice = getIntInput();
            
            if (choice <= 0 || choice > commits.size()) {
                TUIHelper.printInfo("취소되었습니다.");
                return;
            }
            
            RevCommit selectedCommit = commits.get(choice - 1);
            exploreCommit(selectedCommit);
            
        } catch (Exception e) {
            TUIHelper.printError("커밋 탐색 실패: " + e.getMessage());
        }
    }
    
    private List<RevCommit> showCommitList() throws Exception {
        Iterable<RevCommit> commits = git.log().setMaxCount(20).call();
        List<RevCommit> commitList = new java.util.ArrayList<>();
        
        System.out.println("\n최근 20개 커밋:");
        TUIHelper.printDivider();
        
        int count = 0;
        for (RevCommit commit : commits) {
            count++;
            commitList.add(commit);
            
            System.out.printf("[%2d] %s  %s%n",
                count,
                commit.getName().substring(0, 7),
                truncate(commit.getShortMessage(), 50)
            );
            System.out.printf("     %s  %s%n",
                commit.getAuthorIdent().getName(),
                DATE_FORMAT.format(new Date(commit.getCommitTime() * 1000L))
            );
        }
        
        return commitList;
    }
    
    private void exploreCommit(RevCommit commit) throws Exception {
        while (true) {
            TUIHelper.printHeader("커밋 상세 정보", commit.getName().substring(0, 7));
            
            System.out.println("[커밋 정보]");
            System.out.println("  해시: " + commit.getName());
            System.out.println("  작성자: " + commit.getAuthorIdent().getName());
            System.out.println("  이메일: " + commit.getAuthorIdent().getEmailAddress());
            System.out.println("  날짜: " + DATE_FORMAT.format(new Date(commit.getCommitTime() * 1000L)));
            System.out.println("  메시지: " + commit.getFullMessage());
            System.out.println();
            
            System.out.println("[작업 선택]");
            System.out.println("  [1] 변경된 파일 목록 보기");
            System.out.println("  [2] 전체 Diff 보기");
            System.out.println("  [3] 특정 파일 내용 보기");
            System.out.println("  [4] 이 커밋을 체크아웃 (시간 여행!)");
            System.out.println("  [5] 이 커밋의 변경사항을 현재에 적용 (Cherry-pick)");
            System.out.println("  [0] 뒤로 가기");
            System.out.print("선택 > ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1 -> showChangedFiles(commit);
                case 2 -> showFullDiff(commit);
                case 3 -> showFileContent(commit);
                case 4 -> timeTravel(commit);
                case 5 -> cherryPickCommit(commit);
                case 0 -> { return; }
                default -> TUIHelper.printWarning("잘못된 선택입니다.");
            }
            
            if (choice != 0) {
                System.out.print("\nEnter를 눌러 계속...");
                scanner.nextLine();
            }
        }
    }
    
    private void showChangedFiles(RevCommit commit) throws Exception {
        TUIHelper.printStep("변경된 파일 목록:");
        System.out.println();
        
        if (commit.getParentCount() == 0) {
            // 첫 커밋
            TUIHelper.printInfo("첫 커밋입니다 - 모든 파일이 새로 추가됨");
            TreeWalk treeWalk = new TreeWalk(git.getRepository());
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(true);
            
            while (treeWalk.next()) {
                System.out.println("  [추가] " + treeWalk.getPathString());
            }
            treeWalk.close();
            return;
        }
        
        RevCommit parent = commit.getParent(0);
        
        try (DiffFormatter diffFormatter = new DiffFormatter(new ByteArrayOutputStream())) {
            diffFormatter.setRepository(git.getRepository());
            List<DiffEntry> diffs = diffFormatter.scan(parent.getTree(), commit.getTree());
            
            for (DiffEntry diff : diffs) {
                String status = switch (diff.getChangeType()) {
                    case ADD -> "[추가]";
                    case MODIFY -> "[수정]";
                    case DELETE -> "[삭제]";
                    case RENAME -> "[이름변경]";
                    case COPY -> "[복사]";
                };
                
                String path = diff.getNewPath();
                if (diff.getChangeType() == DiffEntry.ChangeType.DELETE) {
                    path = diff.getOldPath();
                }
                
                System.out.println("  " + status + " " + path);
            }
            
            TUIHelper.printInfo("총 " + diffs.size() + "개 파일 변경됨");
        }
    }
    
    private void showFullDiff(RevCommit commit) throws Exception {
        if (commit.getParentCount() == 0) {
            TUIHelper.printWarning("첫 커밋은 Diff를 표시할 수 없습니다.");
            return;
        }
        
        TUIHelper.printStep("전체 변경사항 (Diff):");
        System.out.println();
        
        RevCommit parent = commit.getParent(0);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (DiffFormatter diffFormatter = new DiffFormatter(out)) {
            diffFormatter.setRepository(git.getRepository());
            List<DiffEntry> diffs = diffFormatter.scan(parent.getTree(), commit.getTree());
            
            for (DiffEntry diff : diffs) {
                diffFormatter.format(diff);
            }
        }
        
        String diffOutput = out.toString();
        String[] lines = diffOutput.split("\n");
        
        // 첫 50줄만 표시 (너무 길면)
        int linesToShow = Math.min(lines.length, 50);
        for (int i = 0; i < linesToShow; i++) {
            System.out.println(lines[i]);
        }
        
        if (lines.length > 50) {
            TUIHelper.printInfo("... (" + (lines.length - 50) + "줄 더 있음)");
        }
    }
    
    private void showFileContent(RevCommit commit) throws Exception {
        TUIHelper.printStep("파일 경로를 입력하세요:");
        String filePath = scanner.nextLine().trim();
        
        if (filePath.isEmpty()) {
            TUIHelper.printWarning("파일 경로가 비어있습니다.");
            return;
        }
        
        try (RevWalk revWalk = new RevWalk(git.getRepository())) {
            RevTree tree = commit.getTree();
            TreeWalk treeWalk = TreeWalk.forPath(git.getRepository(), filePath, tree);
            
            if (treeWalk == null) {
                TUIHelper.printError("파일을 찾을 수 없습니다: " + filePath);
                return;
            }
            
            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectReader reader = git.getRepository().newObjectReader();
            byte[] bytes = reader.open(objectId).getBytes();
            String content = new String(bytes);
            
            System.out.println();
            TUIHelper.printSuccess("파일 내용: " + filePath);
            TUIHelper.printDivider();
            
            String[] lines = content.split("\n");
            int linesToShow = Math.min(lines.length, 100);
            
            for (int i = 0; i < linesToShow; i++) {
                System.out.printf("%4d | %s%n", i + 1, lines[i]);
            }
            
            if (lines.length > 100) {
                TUIHelper.printInfo("... (" + (lines.length - 100) + "줄 더 있음)");
            }
            
            reader.close();
            treeWalk.close();
        }
    }
    
    private void timeTravel(RevCommit commit) throws Exception {
        TUIHelper.printWarning("시간 여행 기능!");
        TUIHelper.printWarning("이 커밋 시점으로 작업 디렉토리를 되돌립니다.");
        System.out.print("정말로 진행하시겠습니까? (yes 입력): ");
        
        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            TUIHelper.printInfo("취소되었습니다.");
            return;
        }
        
        git.checkout().setName(commit.getName()).call();
        TUIHelper.printSuccess("시간 여행 완료! " + commit.getName().substring(0, 7) + " 시점으로 이동");
        TUIHelper.printInfo("원래 브랜치로 돌아가려면: git checkout <branch-name>");
    }
    
    private void cherryPickCommit(RevCommit commit) throws Exception {
        TUIHelper.printWarning("Cherry-pick: 이 커밋의 변경사항을 현재 브랜치에 적용합니다");
        System.out.print("진행하시겠습니까? (y/n): ");
        
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            TUIHelper.printInfo("취소되었습니다.");
            return;
        }
        
        git.cherryPick().include(commit).call();
        TUIHelper.printSuccess("Cherry-pick 완료!");
        TUIHelper.printInfo("커밋 " + commit.getName().substring(0, 7) + "의 변경사항이 적용되었습니다");
    }
    
    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}
