package com.supergit.jevi.core;

import org.fusesource.jansi.Ansi;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

/**
 * TUI 헬퍼 - 터미널에 예쁜 UI 출력
 */
public class TUIHelper {
    
    static {
        // JANSI 초기화
        org.fusesource.jansi.AnsiConsole.systemInstall();
    }
    
    public enum BoxStyle {
        INFO, SUCCESS, WARNING, ERROR
    }
    
    /**
     * 박스로 감싼 메시지 출력
     */
    public static void printBox(String message, BoxStyle style) {
        int width = Math.max(60, message.length() + 4);
        String horizontal = "═".repeat(width - 2);
        
        Ansi.Color color = switch (style) {
            case INFO -> CYAN;
            case SUCCESS -> GREEN;
            case WARNING -> YELLOW;
            case ERROR -> RED;
        };
        
        System.out.println(ansi().fg(color).a("╔" + horizontal + "╗").reset());
        System.out.println(ansi().fg(color).a("║ ").reset()
            .bold().a(centerText(message, width - 4)).reset()
            .fg(color).a(" ║").reset());
        System.out.println(ansi().fg(color).a("╚" + horizontal + "╝").reset());
        System.out.println();
    }
    
    /**
     * 진행 상황 표시줄
     */
    public static void printProgress(String label, int current, int total) {
        int barLength = 30;
        int filled = (int) ((double) current / total * barLength);
        String bar = "█".repeat(filled) + "░".repeat(barLength - filled);
        int percentage = (int) ((double) current / total * 100);
        
        System.out.print("\r" + ansi()
            .fg(CYAN).a(label + ": ").reset()
            .fg(GREEN).a("[" + bar + "] ").reset()
            .bold().a(percentage + "%").reset());
        
        if (current == total) {
            System.out.println();
        }
    }
    
    /**
     * 단계 표시
     */
    public static void printStep(String message) {
        System.out.println(ansi()
            .fg(BLUE).a("▶ ").reset()
            .a(message).reset());
    }
    
    /**
     * 성공 메시지
     */
    public static void printSuccess(String message) {
        System.out.println(ansi()
            .fg(GREEN).bold().a("✓ ").reset()
            .fg(GREEN).a(message).reset());
    }
    
    /**
     * 에러 메시지
     */
    public static void printError(String message) {
        System.out.println(ansi()
            .fg(RED).bold().a("✗ ").reset()
            .fg(RED).a(message).reset());
    }
    
    /**
     * 경고 메시지
     */
    public static void printWarning(String message) {
        System.out.println(ansi()
            .fg(YELLOW).bold().a("⚠ ").reset()
            .fg(YELLOW).a(message).reset());
    }
    
    /**
     * 정보 메시지
     */
    public static void printInfo(String message) {
        System.out.println(ansi()
            .fg(CYAN).a("ℹ ").reset()
            .a(message).reset());
    }
    
    /**
     * 헤더 출력
     */
    public static void printHeader(String title, String subtitle) {
        System.out.println();
        System.out.println(ansi()
            .fg(MAGENTA).bold().a("═══════════════════════════════════════════════════════════").reset());
        System.out.println(ansi()
            .fg(MAGENTA).bold().a("  🐦 " + title).reset());
        if (subtitle != null && !subtitle.isEmpty()) {
            System.out.println(ansi()
                .fg(CYAN).a("  " + subtitle).reset());
        }
        System.out.println(ansi()
            .fg(MAGENTA).bold().a("═══════════════════════════════════════════════════════════").reset());
        System.out.println();
    }
    
    /**
     * 구분선
     */
    public static void printDivider() {
        System.out.println(ansi()
            .fg(CYAN).a("───────────────────────────────────────────────────────────").reset());
    }
    
    /**
     * 파일 상태 표시
     */
    public static void printFileStatus(String status, String filename) {
        String icon = switch (status.toUpperCase()) {
            case "MODIFIED" -> "📝";
            case "ADDED" -> "➕";
            case "DELETED" -> "🗑️";
            case "UNTRACKED" -> "❓";
            case "CONFLICTING" -> "⚔️";
            default -> "📄";
        };
        
        Ansi.Color color = switch (status.toUpperCase()) {
            case "MODIFIED" -> YELLOW;
            case "ADDED" -> GREEN;
            case "DELETED" -> RED;
            case "UNTRACKED" -> CYAN;
            case "CONFLICTING" -> MAGENTA;
            default -> WHITE;
        };
        
        System.out.println(ansi()
            .a("  " + icon + " ")
            .fg(color).a(filename).reset());
    }
    
    /**
     * 테이블 행 출력
     */
    public static void printTableRow(String col1, String col2, String col3) {
        System.out.println(ansi()
            .a("  ")
            .fg(CYAN).a(String.format("%-20s", col1)).reset()
            .a("  ")
            .fg(YELLOW).a(String.format("%-30s", col2)).reset()
            .a("  ")
            .fg(GREEN).a(col3).reset());
    }
    
    /**
     * 텍스트 중앙 정렬
     */
    private static String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }
    
    /**
     * 확인 프롬프트
     */
    public static boolean confirm(String message) {
        System.out.print(ansi()
            .fg(YELLOW).a("❓ " + message + " (y/N): ").reset());
        
        try {
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            String response = scanner.nextLine().trim().toLowerCase();
            return response.equals("y") || response.equals("yes");
        } catch (Exception e) {
            return false;
        }
    }
}
