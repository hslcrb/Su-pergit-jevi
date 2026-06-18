package com.supergit.jevi;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 슈퍼 히스토리 - 커밋 히스토리를 예쁘게 표시
 */
@Command(name = "history", 
         aliases = {"log"}, 
         description = "슈퍼하게 커밋 히스토리를 표시합니다")
public class SuperHistoryCommand implements Runnable {

    @Option(names = {"-n", "--number"}, description = "표시할 커밋 개수", defaultValue = "10")
    private int maxCount = 10;

    @Option(names = {"-a", "--all"}, description = "모든 커밋 표시")
    private boolean showAll = false;

    @Override
    public void run() {
        try {
            File currentDir = new File(System.getProperty("user.dir"));
            Git git = Git.open(currentDir);

            System.out.println("🐦 SuperGit-Jevi 커밋 히스토리");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();

            Iterable<RevCommit> commits;
            if (showAll) {
                commits = git.log().all().call();
            } else {
                commits = git.log().setMaxCount(maxCount).call();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int count = 0;

            for (RevCommit commit : commits) {
                count++;
                System.out.println("📌 커밋 #" + count);
                System.out.println("   🔑 해시: " + commit.getName().substring(0, 7));
                System.out.println("   👤 작성자: " + commit.getAuthorIdent().getName());
                System.out.println("   📅 날짜: " + dateFormat.format(new Date(commit.getCommitTime() * 1000L)));
                System.out.println("   💬 메시지: " + commit.getShortMessage());
                System.out.println();
            }

            if (count == 0) {
                System.out.println("아직 커밋이 없습니다.");
            }

            git.close();

        } catch (Exception e) {
            System.err.println("❌ 히스토리 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
