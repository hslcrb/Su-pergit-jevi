package com.supergit.jevi.commands;

import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 히스토리 명령 (최적화됨)
 */
public class HistoryCommand {
    private final Git git;
    private final int maxCount;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public HistoryCommand(Git git, int maxCount) {
        this.git = git;
        this.maxCount = maxCount;
    }
    
    public void execute() {
        try {
            TUIHelper.printHeader("커밋 히스토리", "최근 " + maxCount + "개 커밋");

            Iterable<RevCommit> commits = git.log().setMaxCount(maxCount).call();
            int count = 0;

            for (RevCommit commit : commits) {
                count++;
                
                System.out.printf("  #%-3d %s  %-40s  %s%n",
                    count,
                    commit.getName().substring(0, 7),
                    truncate(commit.getShortMessage(), 40),
                    commit.getAuthorIdent().getName()
                );
                
                System.out.println("       " + DATE_FORMAT.format(
                    new Date(commit.getCommitTime() * 1000L)));
                System.out.println();
            }

            if (count == 0) {
                TUIHelper.printInfo("커밋이 없습니다.");
            } else {
                TUIHelper.printInfo("총 " + count + "개 커밋 표시");
            }

        } catch (Exception e) {
            TUIHelper.printError("히스토리 조회 실패: " + e.getMessage());
        }
    }
    
    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}
