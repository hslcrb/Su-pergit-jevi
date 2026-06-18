package com.supergit.jevi.commands;

import com.supergit.jevi.core.GitSafetyManager;
import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.transport.PushResult;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;

/**
 * 슈퍼 Push - 안전하게 Push하기
 * 반드시 fetch + pull을 먼저 수행
 */
@Command(name = "push", description = "안전하게 원격 저장소에 Push합니다 (자동 fetch+pull)")
public class SuperPushCommand implements Runnable {

    @Option(names = {"-f", "--force"}, description = "강제 push (위험! 안전 검사 무시)")
    private boolean force = false;

    @Option(names = {"-u", "--set-upstream"}, description = "upstream 설정")
    private boolean setUpstream = false;

    @Override
    public void run() {
        try {
            File currentDir = new File(System.getProperty("user.dir"));
            Git git = Git.open(currentDir);

            TUIHelper.printHeader("SuperGit-Jevi Push", "안전한 Push를 위한 자동 동기화");

            // 강제 push 경고
            if (force) {
                TUIHelper.printBox("⚠️ 경고: 강제 Push 모드", TUIHelper.BoxStyle.WARNING);
                TUIHelper.printWarning("안전 검사를 건너뜁니다. 정말 진행하시겠습니까?");
                
                if (!TUIHelper.confirm("강제 Push를 진행하시겠습니까?")) {
                    TUIHelper.printInfo("Push가 취소되었습니다.");
                    return;
                }
            } else {
                // 안전 검사 수행
                GitSafetyManager safetyManager = new GitSafetyManager(git);
                GitSafetyManager.SafetyCheckResult result = safetyManager.performSafetyCheck();

                if (!result.safeToProcceed) {
                    TUIHelper.printBox("🛑 Push 차단됨", TUIHelper.BoxStyle.ERROR);
                    TUIHelper.printError("이유: " + result.message);
                    TUIHelper.printInfo("문제를 해결한 후 다시 시도하세요.");
                    git.close();
                    return;
                }
            }

            // Push 실행
            TUIHelper.printDivider();
            TUIHelper.printStep("3️⃣ 원격 저장소로 Push 중...");
            
            PushCommand pushCommand = git.push();
            if (setUpstream) {
                pushCommand.setRemote("origin");
            }
            
            Iterable<PushResult> results = pushCommand.call();
            
            boolean success = true;
            for (PushResult result : results) {
                if (result.getMessages() != null && !result.getMessages().isEmpty()) {
                    TUIHelper.printInfo("서버 메시지: " + result.getMessages());
                }
            }

            if (success) {
                TUIHelper.printSuccess("✅ Push 완료!");
                TUIHelper.printBox("🎉 성공적으로 원격 저장소에 반영되었습니다!", TUIHelper.BoxStyle.SUCCESS);
            }

            git.close();

        } catch (Exception e) {
            TUIHelper.printError("❌ Push 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
