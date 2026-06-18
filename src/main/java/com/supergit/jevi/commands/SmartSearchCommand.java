package com.supergit.jevi.commands;

import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.util.*;

/**
 * 스마트 검색 - 커밋 메시지, 작성자, 파일 내용에서 검색
 */
public class SmartSearchCommand {
    private final Git git;
    private final Scanner scanner;
    
    public SmartSearchCommand(Git git, Scanner scanner) {
        this.git = git;
        this.scanner = scanner;
    }
    
    public void execute() {
        try {
            TUIHelper.printHeader("스마트 검색", "커밋과 파일에서 원하는 내용을 찾습니다");
            
            System.out.println("[검색 유형 선택]");
            System.out.println("  [1] 커밋 메시지에서 검색");
            System.out.println("  [2] 작성자로 검색");
            System.out.println("  [3] 파일 내용에서 검색 (코드 검색)");
            System.out.println("  [4] 파일 이름으로 검색");
            System.out.println("  [5] 날짜 범위로 검색");
            System.out.println("  [0] 취소");
            System.out.print("선택 > ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1 -> searchInCommitMessage();
                case 2 -> searchByAuthor();
                case 3 -> searchInFileContent();
                case 4 -> searchByFileName();
                case 5 -> searchByDateRange();
                case 0 -> TUIHelper.printInfo("취소되었습니다.");
                default -> TUIHelper.printWarning("잘못된 선택입니다.");
            }
            
        } catch (Exception e) {
            TUIHelper.printError("검색 실패: " + e.getMessage());
        }
    }
    
    private void searchInCommitMessage() throws Exception {
        System.out.print("검색할 키워드: ");
        String keyword = scanner.nextLine().trim().toLowerCase();
        
        if (keyword.isEmpty()) {
            TUIHelper.printWarning("키워드가 비어있습니다.");
            return;
        }
        
        TUIHelper.printStep("검색 중...");
        
        Iterable<RevCommit> commits = git.log().all().call();
        List<RevCommit> results = new ArrayList<>();
        
        for (RevCommit commit : commits) {
            if (commit.getFullMessage().toLowerCase().contains(keyword)) {
                results.add(commit);
            }
        }
        
        displaySearchResults(results, "커밋 메시지에 '" + keyword + "' 포함");
    }
    
    private void searchByAuthor() throws Exception {
        System.out.print("작성자 이름 (일부만 입력 가능): ");
        String author = scanner.nextLine().trim().toLowerCase();
        
        if (author.isEmpty()) {
            TUIHelper.printWarning("작성자 이름이 비어있습니다.");
            return;
        }
        
        TUIHelper.printStep("검색 중...");
        
        Iterable<RevCommit> commits = git.log().all().call();
        List<RevCommit> results = new ArrayList<>();
        
        for (RevCommit commit : commits) {
            if (commit.getAuthorIdent().getName().toLowerCase().contains(author)) {
                results.add(commit);
            }
        }
        
        displaySearchResults(results, "작성자 '" + author + "'");
    }
    
    private void searchInFileContent() throws Exception {
        System.out.print("검색할 코드/텍스트: ");
        String keyword = scanner.nextLine().trim();
        
        if (keyword.isEmpty()) {
            TUIHelper.printWarning("검색어가 비어있습니다.");
            return;
        }
        
        TUIHelper.printStep("현재 작업 디렉토리에서 검색 중...");
        
        Map<String, List<Integer>> results = new HashMap<>();
        
        RevCommit latestCommit = git.log().setMaxCount(1).call().iterator().next();
        RevTree tree = latestCommit.getTree();
        
        try (TreeWalk treeWalk = new TreeWalk(git.getRepository())) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            
            ObjectReader reader = git.getRepository().newObjectReader();
            
            while (treeWalk.next()) {
                String path = treeWalk.getPathString();
                
                // 텍스트 파일만 검색
                if (path.endsWith(".class") || path.endsWith(".jar") || 
                    path.endsWith(".png") || path.endsWith(".jpg")) {
                    continue;
                }
                
                try {
                    ObjectId objectId = treeWalk.getObjectId(0);
                    byte[] bytes = reader.open(objectId).getBytes();
                    String content = new String(bytes);
                    
                    List<Integer> lineNumbers = new ArrayList<>();
                    String[] lines = content.split("\n");
                    
                    for (int i = 0; i < lines.length; i++) {
                        if (lines[i].contains(keyword)) {
                            lineNumbers.add(i + 1);
                        }
                    }
                    
                    if (!lineNumbers.isEmpty()) {
                        results.put(path, lineNumbers);
                    }
                } catch (Exception e) {
                    // 바이너리 파일 등은 스킵
                }
            }
            
            reader.close();
        }
        
        System.out.println();
        if (results.isEmpty()) {
            TUIHelper.printWarning("검색 결과가 없습니다.");
        } else {
            TUIHelper.printSuccess(results.size() + "개 파일에서 발견!");
            System.out.println();
            
            for (Map.Entry<String, List<Integer>> entry : results.entrySet()) {
                System.out.println("[파일] " + entry.getKey());
                System.out.print("  줄 번호: ");
                for (int i = 0; i < Math.min(entry.getValue().size(), 10); i++) {
                    System.out.print(entry.getValue().get(i));
                    if (i < Math.min(entry.getValue().size(), 10) - 1) {
                        System.out.print(", ");
                    }
                }
                if (entry.getValue().size() > 10) {
                    System.out.print(" ... (+" + (entry.getValue().size() - 10) + "개 더)");
                }
                System.out.println();
            }
        }
    }
    
    private void searchByFileName() throws Exception {
        System.out.print("파일 이름 (일부만 입력 가능): ");
        String fileName = scanner.nextLine().trim().toLowerCase();
        
        if (fileName.isEmpty()) {
            TUIHelper.printWarning("파일 이름이 비어있습니다.");
            return;
        }
        
        TUIHelper.printStep("검색 중...");
        
        RevCommit latestCommit = git.log().setMaxCount(1).call().iterator().next();
        RevTree tree = latestCommit.getTree();
        
        List<String> results = new ArrayList<>();
        
        try (TreeWalk treeWalk = new TreeWalk(git.getRepository())) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            
            while (treeWalk.next()) {
                String path = treeWalk.getPathString();
                if (path.toLowerCase().contains(fileName)) {
                    results.add(path);
                }
            }
        }
        
        System.out.println();
        if (results.isEmpty()) {
            TUIHelper.printWarning("검색 결과가 없습니다.");
        } else {
            TUIHelper.printSuccess(results.size() + "개 파일 발견!");
            System.out.println();
            
            for (String path : results) {
                System.out.println("  - " + path);
            }
        }
    }
    
    private void searchByDateRange() throws Exception {
        System.out.print("시작 날짜 (YYYY-MM-DD): ");
        String startDate = scanner.nextLine().trim();
        System.out.print("종료 날짜 (YYYY-MM-DD): ");
        String endDate = scanner.nextLine().trim();
        
        TUIHelper.printStep("검색 중...");
        
        // 간단한 날짜 파싱 (YYYY-MM-DD)
        long startTime = parseDate(startDate);
        long endTime = parseDate(endDate) + 86400; // 하루 추가
        
        Iterable<RevCommit> commits = git.log().all().call();
        List<RevCommit> results = new ArrayList<>();
        
        for (RevCommit commit : commits) {
            long commitTime = commit.getCommitTime();
            if (commitTime >= startTime && commitTime <= endTime) {
                results.add(commit);
            }
        }
        
        displaySearchResults(results, startDate + " ~ " + endDate);
    }
    
    private void displaySearchResults(List<RevCommit> results, String criteria) {
        System.out.println();
        if (results.isEmpty()) {
            TUIHelper.printWarning("검색 결과가 없습니다.");
        } else {
            TUIHelper.printSuccess(results.size() + "개 커밋 발견! (" + criteria + ")");
            System.out.println();
            
            for (int i = 0; i < Math.min(results.size(), 20); i++) {
                RevCommit commit = results.get(i);
                System.out.printf("[%2d] %s  %s%n",
                    i + 1,
                    commit.getName().substring(0, 7),
                    truncate(commit.getShortMessage(), 50)
                );
                System.out.printf("     %s%n",
                    commit.getAuthorIdent().getName()
                );
            }
            
            if (results.size() > 20) {
                TUIHelper.printInfo("... (+" + (results.size() - 20) + "개 더)");
            }
        }
    }
    
    private long parseDate(String dateStr) {
        try {
            String[] parts = dateStr.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, day, 0, 0, 0);
            return cal.getTimeInMillis() / 1000;
        } catch (Exception e) {
            return 0;
        }
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
