package com.supergit.jevi.commands;

import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Scanner;

/**
 * 스마트 비교 - 다양한 방식으로 변경사항 비교
 */
public class SmartDiffCommand {
    private final Git git;
    private final Scanner scanner;
    
    public SmartDiffCommand(Git git, Scanner scanner) {
        this.git = git;
        this.scanner = scanner;
    }
    
    public void execute() {
        try {
            TUIHelper.printHeader("스마트 비교", "변경사항을 다양하게 비교합니다");
            
            System.out.println("[비교 모드 선택]");
            System.out.println("  [1] 작업 디렉토리 vs 마지막 커밋");
            System.out.println("  [2] 두 커밋 비교");
            System.out.println("  [3] 브랜치 간 비교");
            System.out.println("  [4] 특정 파일만 비교");
            System.out.println("  [5] 통계 정보 보기");
            System.out.println("  [0] 취소");
            System.out.print("선택 > ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1 -> diffWorkingTree();
                case 2 -> diffTwoCommits();
                case 3 -> diffBranches();
                case 4 -> diffSpecificFile();
                case 5 -> showDiffStats();
                case 0 -> TUIHelper.printInfo("취소되었습니다.");
                default -> TUIHelper.printWarning("잘못된 선택입니다.");
            }
            
        } catch (Exception e) {
            TUIHelper.printError("비교 실패: " + e.getMessage());
        }
    }
    
    private void diffWorkingTree() throws Exception {
        TUIHelper.printStep("작업 디렉토리와 마지막 커밋 비교 중...");
        System.out.println();
        
        RevWalk walk = new RevWalk(git.getRepository());
        RevCommit head = walk.parseCommit(git.getRepository().resolve("HEAD"));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (DiffFormatter formatter = new DiffFormatter(out)) {
            formatter.setRepository(git.getRepository());
            
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            ObjectReader reader = git.getRepository().newObjectReader();
            oldTreeIter.reset(reader, head.getTree());
            
            formatter.setPathFilter(null);
            List<DiffEntry> diffs = formatter.scan(oldTreeIter, null);
            
            if (diffs.isEmpty()) {
                TUIHelper.printInfo("변경사항이 없습니다.");
                return;
            }
            
            for (DiffEntry diff : diffs) {
                formatter.format(diff);
            }
            
            reader.close();
        }
        walk.close();
        
        System.out.println(out.toString());
    }
    
    private void diffTwoCommits() throws Exception {
        System.out.print("첫 번째 커밋 해시 (앞 7자리만 입력 가능): ");
        String hash1 = scanner.nextLine().trim();
        System.out.print("두 번째 커밋 해시: ");
        String hash2 = scanner.nextLine().trim();
        
        TUIHelper.printStep("두 커밋 비교 중...");
        System.out.println();
        
        RevWalk walk = new RevWalk(git.getRepository());
        RevCommit commit1 = walk.parseCommit(git.getRepository().resolve(hash1));
        RevCommit commit2 = walk.parseCommit(git.getRepository().resolve(hash2));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (DiffFormatter formatter = new DiffFormatter(out)) {
            formatter.setRepository(git.getRepository());
            formatter.format(commit1, commit2);
        }
        walk.close();
        
        System.out.println(out.toString());
    }
    
    private void diffBranches() throws Exception {
        System.out.print("첫 번째 브랜치: ");
        String branch1 = scanner.nextLine().trim();
        System.out.print("두 번째 브랜치: ");
        String branch2 = scanner.nextLine().trim();
        
        TUIHelper.printStep("브랜치 비교 중...");
        System.out.println();
        
        RevWalk walk = new RevWalk(git.getRepository());
        RevCommit commit1 = walk.parseCommit(git.getRepository().resolve(branch1));
        RevCommit commit2 = walk.parseCommit(git.getRepository().resolve(branch2));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (DiffFormatter formatter = new DiffFormatter(out)) {
            formatter.setRepository(git.getRepository());
            formatter.format(commit1, commit2);
        }
        walk.close();
        
        System.out.println(out.toString());
    }
    
    private void diffSpecificFile() throws Exception {
        System.out.print("파일 경로: ");
        String filePath = scanner.nextLine().trim();
        
        TUIHelper.printStep("파일 변경사항 비교 중...");
        System.out.println();
        
        RevWalk walk = new RevWalk(git.getRepository());
        RevCommit head = walk.parseCommit(git.getRepository().resolve("HEAD"));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (DiffFormatter formatter = new DiffFormatter(out)) {
            formatter.setRepository(git.getRepository());
            formatter.setPathFilter(org.eclipse.jgit.treewalk.filter.PathFilter.create(filePath));
            
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            ObjectReader reader = git.getRepository().newObjectReader();
            oldTreeIter.reset(reader, head.getTree());
            
            List<DiffEntry> diffs = formatter.scan(oldTreeIter, null);
            
            for (DiffEntry diff : diffs) {
                formatter.format(diff);
            }
            
            reader.close();
        }
        walk.close();
        
        String output = out.toString();
        if (output.isEmpty()) {
            TUIHelper.printInfo("해당 파일의 변경사항이 없습니다.");
        } else {
            System.out.println(output);
        }
    }
    
    private void showDiffStats() throws Exception {
        TUIHelper.printStep("변경사항 통계 수집 중...");
        System.out.println();
        
        RevWalk walk = new RevWalk(git.getRepository());
        RevCommit head = walk.parseCommit(git.getRepository().resolve("HEAD"));
        
        try (DiffFormatter formatter = new DiffFormatter(new ByteArrayOutputStream())) {
            formatter.setRepository(git.getRepository());
            
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            ObjectReader reader = git.getRepository().newObjectReader();
            oldTreeIter.reset(reader, head.getTree());
            
            List<DiffEntry> diffs = formatter.scan(oldTreeIter, null);
            
            int added = 0, modified = 0, deleted = 0;
            
            for (DiffEntry diff : diffs) {
                switch (diff.getChangeType()) {
                    case ADD -> added++;
                    case MODIFY -> modified++;
                    case DELETE -> deleted++;
                }
            }
            
            TUIHelper.printSuccess("변경사항 통계:");
            System.out.println("  [추가] " + added + "개 파일");
            System.out.println("  [수정] " + modified + "개 파일");
            System.out.println("  [삭제] " + deleted + "개 파일");
            System.out.println("  [총합] " + diffs.size() + "개 파일");
            
            reader.close();
        }
        walk.close();
    }
    
    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
