package com.supergit.jevi;

import com.supergit.jevi.ui.InteractiveMenu;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * SuperGit-Jevi 메인 클래스
 * Git을 슈퍼하게 사용하는 도구
 */
@Command(name = "jevi", 
         mixinStandardHelpOptions = true, 
         version = "SuperGit-Jevi 1.0.0",
         description = "슈퍼하게 Git을 사용하는 제비처럼 빠른 도구")
public class SuperGitJevi implements Runnable {

    @Override
    public void run() {
        // 대화형 메뉴 시작
        InteractiveMenu menu = new InteractiveMenu();
        menu.start();
    }

    public static void main(String[] args) {
        // 인자가 없으면 대화형 모드
        if (args.length == 0) {
            new SuperGitJevi().run();
        } else {
            // 명령줄 인자가 있으면 기존 방식
            int exitCode = new CommandLine(new SuperGitJevi()).execute(args);
            System.exit(exitCode);
        }
    }
}
