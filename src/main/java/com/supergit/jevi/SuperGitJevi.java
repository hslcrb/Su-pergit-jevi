package com.supergit.jevi;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * SuperGit-Jevi 메인 클래스
 * Git을 슈퍼하게 사용하는 도구
 */
@Command(name = "jevi", 
         mixinStandardHelpOptions = true, 
         version = "SuperGit-Jevi 1.0.0",
         description = "슈퍼하게 Git을 사용하는 제비처럼 빠른 도구",
         subcommands = {
             SuperCommitCommand.class,
             SuperStatusCommand.class,
             SuperBranchCommand.class,
             SuperHistoryCommand.class,
             com.supergit.jevi.commands.SuperPushCommand.class
         })
public class SuperGitJevi implements Runnable {

    @Override
    public void run() {
        com.supergit.jevi.core.TUIHelper.printBox("🐦 SuperGit-Jevi에 오신 것을 환영합니다!", 
            com.supergit.jevi.core.TUIHelper.BoxStyle.INFO);
        
        System.out.println("슈퍼한 Git 경험을 제공하는 TUI 도구입니다.");
        System.out.println();
        
        com.supergit.jevi.core.TUIHelper.printInfo("📌 사용 가능한 명령어:");
        System.out.println("  • jevi status     - 저장소 상태 확인");
        System.out.println("  • jevi commit     - 변경사항 커밋");
        System.out.println("  • jevi push       - 안전하게 Push (자동 fetch+pull)");
        System.out.println("  • jevi branch     - 브랜치 관리");
        System.out.println("  • jevi history    - 커밋 히스토리");
        System.out.println();
        
        com.supergit.jevi.core.TUIHelper.printSuccess("더 많은 정보: jevi --help");
        System.out.println();
        
        com.supergit.jevi.core.TUIHelper.printBox("🛡️ 안전 기능: Push 전 자동 fetch+pull로 충돌 방지!", 
            com.supergit.jevi.core.TUIHelper.BoxStyle.SUCCESS);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new SuperGitJevi()).execute(args);
        System.exit(exitCode);
    }
}
