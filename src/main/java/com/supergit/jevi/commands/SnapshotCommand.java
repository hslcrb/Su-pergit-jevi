package com.supergit.jevi.commands;

import com.supergit.jevi.core.TUIHelper;
import com.supergit.jevi.snapshot.SnapshotGenerator;
import com.supergit.jevi.snapshot.SnapshotData;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * Snapshot Command - AI Agent를 위한 통합 저장소 스냅샷
 * 
 * 저장소의 모든 히스토리, 파일 변경, 브랜치 구조를 하나의 통합된 뷰로 제공
 */
public class SnapshotCommand {
    private final Git git;
    private final Scanner scanner;
    
    public SnapshotCommand(Git git, Scanner scanner) {
        this.git = git;
        this.scanner = scanner;
    }
    
    public void execute() {
        try {
            TUIHelper.printHeader("Repository Snapshot (for AI)", 
                "AI Agent를 위한 통합 저장소 스냅샷");
            
            System.out.println("[출력 형식 선택]");
            System.out.println("  [1] JSON (for AI) - 구조화된 데이터");
            System.out.println("  [2] TEXT (for Human) - 읽기 쉬운 형식");
            System.out.println("  [3] FULL - 파일 내용 포함 (대용량)");
            System.out.println("  [4] 특정 파일 추적");
            System.out.println("  [0] 취소");
            System.out.print("선택 > ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1 -> generateJson(false);
                case 2 -> generateText(false);
                case 3 -> generateFull();
                case 4 -> trackSpecificFile();
                case 0 -> TUIHelper.printInfo("취소되었습니다.");
                default -> TUIHelper.printWarning("잘못된 선택입니다.");
            }
            
        } catch (Exception e) {
            TUIHelper.printError("스냅샷 생성 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void generateJson(boolean full) throws Exception {
        TUIHelper.printStep("JSON 스냅샷 생성 중...");
        
        SnapshotGenerator generator = new SnapshotGenerator(git);
        SnapshotData data = generator.generate(full);
        
        String json = data.toJson();
        
        System.out.print("\n파일로 저장하시겠습니까? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.print("파일 이름 (기본: snapshot.json): ");
            String filename = scanner.nextLine().trim();
            if (filename.isEmpty()) {
                filename = "snapshot.json";
            }
            
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(json);
            }
            
            TUIHelper.printSuccess("저장 완료: " + filename);
        } else {
            System.out.println("\n=== JSON OUTPUT ===\n");
            System.out.println(json);
        }
    }
    
    private void generateText(boolean full) throws Exception {
        TUIHelper.printStep("텍스트 스냅샷 생성 중...");
        
        SnapshotGenerator generator = new SnapshotGenerator(git);
        SnapshotData data = generator.generate(full);
        
        String text = data.toText();
        
        System.out.print("\n파일로 저장하시겠습니까? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.print("파일 이름 (기본: snapshot.txt): ");
            String filename = scanner.nextLine().trim();
            if (filename.isEmpty()) {
                filename = "snapshot.txt";
            }
            
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(text);
            }
            
            TUIHelper.printSuccess("저장 완료: " + filename);
        } else {
            System.out.println("\n" + text);
        }
    }
    
    private void generateFull() throws Exception {
        TUIHelper.printWarning("FULL 모드는 모든 파일 내용을 포함하여 매우 큰 출력을 생성합니다.");
        System.out.print("계속하시겠습니까? (y/n): ");
        
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            TUIHelper.printInfo("취소되었습니다.");
            return;
        }
        
        generateJson(true);
    }
    
    private void trackSpecificFile() throws Exception {
        System.out.print("추적할 파일 경로: ");
        String filePath = scanner.nextLine().trim();
        
        if (filePath.isEmpty()) {
            TUIHelper.printWarning("파일 경로가 비어있습니다.");
            return;
        }
        
        TUIHelper.printStep("파일 히스토리 추적 중: " + filePath);
        
        SnapshotGenerator generator = new SnapshotGenerator(git);
        String fileHistory = generator.trackFile(filePath);
        
        System.out.println("\n" + fileHistory);
        
        System.out.print("\n파일로 저장하시겠습니까? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            String filename = filePath.replace("/", "_") + "_history.txt";
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(fileHistory);
            }
            TUIHelper.printSuccess("저장 완료: " + filename);
        }
    }
    
    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
