package com.supergit.jevi.snapshot;

import com.supergit.jevi.core.TUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 스냅샷 생성기 - 저장소의 모든 정보를 수집하여 통합
 */
public class SnapshotGenerator {
    private final Git git;
    private static final SimpleDateFormat DATE_FORMAT = 
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    
    public SnapshotGenerator(Git git) {
        this.git = git;
    }
    
    /**
     * 전체 스냅샷 생성
     */
    public SnapshotData generate(boolean includeFileContents) throws Exception {
        SnapshotData data = new SnapshotData();
        
        TUIHelper.printInfo("[1/6] 저장소 정보 수집 중...");
        data.repository = collectRepositoryInfo();
        
        TUIHelper.printInfo("[2/6] 커밋 히스토리 수집 중...");
        data.commits = collectCommits();
        
        TUIHelper.printInfo("[3/6] 파일 시스템 분석 중...");
        data.fileSystem = collectFileSystem(data.commits);
        
        TUIHelper.printInfo("[4/6] 브랜치 구조 분석 중...");
        data.branches = collectBranches();
        
        TUIHelper.printInfo("[5/6] 통계 계산 중...");
        data.statistics = calculateStatistics(data);
        
        TUIHelper.printInfo("[6/6] 데이터 정리 중...");
        
        TUIHelper.printSuccess("스냅샷 생성 완료!");
        return data;
    }
    
    /**
     * 특정 파일의 전체 히스토리 추적
     */
    public String trackFile(String filePath) throws Exception {
        StringBuilder history = new StringBuilder();
        
        history.append(String.format("=== File History: %s ===\n\n", filePath));
        
        Iterable<RevCommit> commits = git.log().addPath(filePath).call();
        
        int count = 0;
        for (RevCommit commit : commits) {
            count++;
            history.append(String.format("[%d] %s - %s\n", 
                count, 
                commit.getName().substring(0, 7),
                DATE_FORMAT.format(new Date(commit.getCommitTime() * 1000L))
            ));
            history.append(String.format("    Author: %s\n", commit.getAuthorIdent().getName()));
            history.append(String.format("    Message: %s\n", commit.getShortMessage()));
            
            if (commit.getParentCount() > 0) {
                RevCommit parent = commit.getParent(0);
                String action = determineFileAction(commit, parent, filePath);
                history.append(String.format("    Action: %s\n", action));
            } else {
                history.append("    Action: CREATED\n");
            }
            
            history.append("\n");
        }
        
        if (count == 0) {
            history.append("파일이 존재하지 않거나 히스토리가 없습니다.\n");
        } else {
            history.append(String.format("총 %d개의 커밋에서 변경됨\n", count));
        }
        
        return history.toString();
    }
    
    private SnapshotData.RepositoryInfo collectRepositoryInfo() throws Exception {
        SnapshotData.RepositoryInfo info = new SnapshotData.RepositoryInfo();
        
        File workTree = git.getRepository().getWorkTree();
        info.name = workTree.getName();
        info.currentBranch = git.getRepository().getBranch();
        
        int commitCount = 0;
        for (RevCommit commit : git.log().all().call()) {
            commitCount++;
        }
        info.totalCommits = commitCount;
        
        return info;
    }
    
    private List<SnapshotData.CommitInfo> collectCommits() throws Exception {
        List<SnapshotData.CommitInfo> commits = new ArrayList<>();
        
        Iterable<RevCommit> log = git.log().all().call();
        
        for (RevCommit commit : log) {
            SnapshotData.CommitInfo info = new SnapshotData.CommitInfo();
            
            info.hash = commit.getName();
            info.shortHash = commit.getName().substring(0, 7);
            info.author = commit.getAuthorIdent().getName();
            info.email = commit.getAuthorIdent().getEmailAddress();
            info.date = DATE_FORMAT.format(new Date(commit.getCommitTime() * 1000L));
            info.message = commit.getShortMessage();
            info.description = commit.getFullMessage().replace(commit.getShortMessage(), "").trim();
            info.mergeCommit = commit.getParentCount() > 1;
            
            for (RevCommit parent : commit.getParents()) {
                info.parents.add(parent.getName());
            }
            
            // 변경된 파일 수집
            if (commit.getParentCount() > 0) {
                info.changedFiles = getChangedFiles(commit, commit.getParent(0));
            }
            
            commits.add(info);
        }
        
        return commits;
    }
    
    private List<SnapshotData.FileChange> getChangedFiles(RevCommit commit, RevCommit parent) 
            throws Exception {
        List<SnapshotData.FileChange> changes = new ArrayList<>();
        
        try (DiffFormatter formatter = new DiffFormatter(new ByteArrayOutputStream())) {
            formatter.setRepository(git.getRepository());
            List<DiffEntry> diffs = formatter.scan(parent.getTree(), commit.getTree());
            
            for (DiffEntry diff : diffs) {
                SnapshotData.FileChange change = new SnapshotData.FileChange();
                change.path = diff.getNewPath();
                change.status = diff.getChangeType().name();
                change.additions = 0; // JGit에서 직접 가져오기 어려움
                change.deletions = 0;
                
                if (diff.getChangeType() == DiffEntry.ChangeType.DELETE) {
                    change.path = diff.getOldPath();
                }
                
                changes.add(change);
            }
        }
        
        return changes;
    }
    
    private SnapshotData.FileSystemInfo collectFileSystem(List<SnapshotData.CommitInfo> commits) 
            throws Exception {
        SnapshotData.FileSystemInfo fs = new SnapshotData.FileSystemInfo();
        
        // 현재 파일 시스템
        RevCommit latestCommit = git.log().setMaxCount(1).call().iterator().next();
        RevTree tree = latestCommit.getTree();
        
        try (TreeWalk treeWalk = new TreeWalk(git.getRepository())) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            
            while (treeWalk.next()) {
                String path = treeWalk.getPathString();
                SnapshotData.CurrentFile file = new SnapshotData.CurrentFile();
                file.exists = true;
                file.size = treeWalk.getObjectReader().getSize(
                    treeWalk.getObjectId(0), org.eclipse.jgit.lib.Constants.OBJ_BLOB);
                file.lastModified = DATE_FORMAT.format(
                    new Date(latestCommit.getCommitTime() * 1000L));
                
                fs.current.put(path, file);
            }
        }
        
        // 파일 히스토리 구축
        Map<String, Set<String>> allFiles = new HashMap<>(); // 파일 -> 커밋 Set
        
        for (SnapshotData.CommitInfo commit : commits) {
            for (SnapshotData.FileChange change : commit.changedFiles) {
                allFiles.computeIfAbsent(change.path, k -> new HashSet<>()).add(commit.shortHash);
                
                SnapshotData.FileEvent event = new SnapshotData.FileEvent();
                event.commit = commit.shortHash;
                event.action = change.status;
                event.date = commit.date;
                
                fs.history.computeIfAbsent(change.path, k -> new ArrayList<>()).add(event);
            }
        }
        
        // 삭제된 파일 찾기
        for (Map.Entry<String, List<SnapshotData.FileEvent>> entry : fs.history.entrySet()) {
            String path = entry.getKey();
            if (!fs.current.containsKey(path)) {
                // 삭제된 파일
                List<SnapshotData.FileEvent> events = entry.getValue();
                if (!events.isEmpty()) {
                    SnapshotData.FileEvent lastEvent = events.get(events.size() - 1);
                    if (lastEvent.action.equals("DELETE") || lastEvent.action.equals("DELETED")) {
                        SnapshotData.DeletedFile deleted = new SnapshotData.DeletedFile();
                        deleted.deletedIn = lastEvent.commit;
                        deleted.deletedDate = lastEvent.date;
                        deleted.existedFor = calculateDuration(events);
                        
                        fs.deleted.put(path, deleted);
                    }
                }
            }
        }
        
        return fs;
    }
    
    private SnapshotData.BranchInfo collectBranches() throws Exception {
        SnapshotData.BranchInfo info = new SnapshotData.BranchInfo();
        
        List<Ref> branches = git.branchList().call();
        
        for (Ref ref : branches) {
            String name = ref.getName().replace("refs/heads/", "");
            info.all.add(name);
            info.active.add(name); // 간단화: 모든 브랜치를 active로
        }
        
        return info;
    }
    
    private SnapshotData.Statistics calculateStatistics(SnapshotData data) {
        SnapshotData.Statistics stats = new SnapshotData.Statistics();
        
        stats.totalCommits = data.commits.size();
        stats.activeFiles = data.fileSystem.current.size();
        stats.deletedFiles = data.fileSystem.deleted.size();
        stats.totalFiles = stats.activeFiles + stats.deletedFiles;
        
        Set<String> contributors = new HashSet<>();
        for (SnapshotData.CommitInfo commit : data.commits) {
            contributors.add(commit.author);
        }
        stats.contributors = contributors.size();
        
        return stats;
    }
    
    private String determineFileAction(RevCommit commit, RevCommit parent, String filePath) 
            throws Exception {
        try (DiffFormatter formatter = new DiffFormatter(new ByteArrayOutputStream())) {
            formatter.setRepository(git.getRepository());
            formatter.setPathFilter(org.eclipse.jgit.treewalk.filter.PathFilter.create(filePath));
            
            List<DiffEntry> diffs = formatter.scan(parent.getTree(), commit.getTree());
            
            if (!diffs.isEmpty()) {
                return diffs.get(0).getChangeType().name();
            }
        }
        
        return "UNKNOWN";
    }
    
    private String calculateDuration(List<SnapshotData.FileEvent> events) {
        if (events.size() < 2) return "unknown";
        
        try {
            Date first = DATE_FORMAT.parse(events.get(0).date);
            Date last = DATE_FORMAT.parse(events.get(events.size() - 1).date);
            
            long days = (last.getTime() - first.getTime()) / (1000 * 60 * 60 * 24);
            return days + " days";
        } catch (Exception e) {
            return "unknown";
        }
    }
}
