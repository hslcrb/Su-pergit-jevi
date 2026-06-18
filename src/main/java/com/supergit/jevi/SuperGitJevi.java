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
             SuperHistoryCommand.class
         })
public class SuperGitJevi implements Runnable {

    @Override
    public void run() {
        System.out.println("🐦 SuperGit-Jevi에 오신 것을 환영합니다!");
        System.out.println("슈퍼한 Git 경험을 제공합니다.");
        System.out.println();
        System.out.println("사용법: jevi <command> [options]");
        System.out.println("도움말: jevi --help");
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new SuperGitJevi()).execute(args);
        System.exit(exitCode);
    }
}
