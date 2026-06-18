package com.supergit.jevi.snapshot;

import java.util.*;

/**
 * 스냅샷 데이터 구조
 */
public class SnapshotData {
    public RepositoryInfo repository;
    public List<CommitInfo> commits;
    public FileSystemInfo fileSystem;
    public BranchInfo branches;
    public Statistics statistics;
    
    public SnapshotData() {
        this.commits = new ArrayList<>();
        this.fileSystem = new FileSystemInfo();
        this.branches = new BranchInfo();
        this.statistics = new Statistics();
    }
    
    /**
     * JSON 형식으로 변환 (AI 최적화)
     */
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"repository\": ").append(repository.toJson()).append(",\n");
        json.append("  \"commits\": [\n");
        
        for (int i = 0; i < commits.size(); i++) {
            json.append("    ").append(commits.get(i).toJson());
            if (i < commits.size() - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("  ],\n");
        json.append("  \"fileSystem\": ").append(fileSystem.toJson()).append(",\n");
        json.append("  \"branches\": ").append(branches.toJson()).append(",\n");
        json.append("  \"statistics\": ").append(statistics.toJson()).append("\n");
        json.append("}\n");
        
        return json.toString();
    }
    
    /**
     * 텍스트 형식으로 변환 (Human 친화적)
     */
    public String toText() {
        StringBuilder text = new StringBuilder();
        
        text.append("=== Repository Snapshot ===\n");
        text.append(repository.toText()).append("\n\n");
        
        text.append("=== Commit History ===\n\n");
        for (int i = 0; i < commits.size(); i++) {
            text.append(String.format("[%d] ", i + 1));
            text.append(commits.get(i).toText());
            text.append("\n---\n\n");
        }
        
        text.append("=== File System ===\n");
        text.append(fileSystem.toText()).append("\n\n");
        
        text.append("=== Branch Structure ===\n");
        text.append(branches.toText()).append("\n\n");
        
        text.append("=== Statistics ===\n");
        text.append(statistics.toText()).append("\n");
        
        return text.toString();
    }
    
    public static class RepositoryInfo {
        public String name;
        public String currentBranch;
        public int totalCommits;
        
        public String toJson() {
            return String.format("{\"name\":\"%s\",\"currentBranch\":\"%s\",\"totalCommits\":%d}",
                name, currentBranch, totalCommits);
        }
        
        public String toText() {
            return String.format("Repository: %s\nBranch: %s\nTotal Commits: %d",
                name, currentBranch, totalCommits);
        }
    }
    
    public static class CommitInfo {
        public String hash;
        public String shortHash;
        public String author;
        public String email;
        public String date;
        public String message;
        public String description;
        public List<String> parents;
        public boolean mergeCommit;
        public List<FileChange> changedFiles;
        
        public CommitInfo() {
            parents = new ArrayList<>();
            changedFiles = new ArrayList<>();
        }
        
        public String toJson() {
            StringBuilder json = new StringBuilder("{");
            json.append(String.format("\"hash\":\"%s\",", hash));
            json.append(String.format("\"shortHash\":\"%s\",", shortHash));
            json.append(String.format("\"author\":\"%s\",", escapeJson(author)));
            json.append(String.format("\"email\":\"%s\",", email));
            json.append(String.format("\"date\":\"%s\",", date));
            json.append(String.format("\"message\":\"%s\",", escapeJson(message)));
            json.append(String.format("\"description\":\"%s\",", escapeJson(description)));
            json.append(String.format("\"mergeCommit\":%b,", mergeCommit));
            json.append("\"changedFiles\":[");
            
            for (int i = 0; i < changedFiles.size(); i++) {
                json.append(changedFiles.get(i).toJson());
                if (i < changedFiles.size() - 1) json.append(",");
            }
            
            json.append("]}");
            return json.toString();
        }
        
        public String toText() {
            StringBuilder text = new StringBuilder();
            text.append(String.format("%s - %s\n", shortHash, date));
            text.append(String.format("Author: %s <%s>\n", author, email));
            if (mergeCommit) text.append("[MERGE COMMIT]\n");
            text.append(String.format("Message: %s\n", message));
            if (!description.isEmpty()) {
                text.append(String.format("\n%s\n", description));
            }
            text.append("\nChanged Files:\n");
            for (FileChange fc : changedFiles) {
                text.append(String.format("  %s %s\n", fc.status, fc.path));
            }
            return text.toString();
        }
        
        private String escapeJson(String str) {
            if (str == null) return "";
            return str.replace("\\", "\\\\")
                     .replace("\"", "\\\"")
                     .replace("\n", "\\n")
                     .replace("\r", "\\r")
                     .replace("\t", "\\t");
        }
    }
    
    public static class FileChange {
        public String path;
        public String status; // ADDED, MODIFIED, DELETED
        public int additions;
        public int deletions;
        
        public String toJson() {
            return String.format("{\"path\":\"%s\",\"status\":\"%s\",\"additions\":%d,\"deletions\":%d}",
                path, status, additions, deletions);
        }
    }
    
    public static class FileSystemInfo {
        public Map<String, CurrentFile> current;
        public Map<String, List<FileEvent>> history;
        public Map<String, DeletedFile> deleted;
        
        public FileSystemInfo() {
            current = new HashMap<>();
            history = new HashMap<>();
            deleted = new HashMap<>();
        }
        
        public String toJson() {
            StringBuilder json = new StringBuilder("{");
            json.append("\"current\":{");
            
            int i = 0;
            for (Map.Entry<String, CurrentFile> entry : current.entrySet()) {
                json.append(String.format("\"%s\":%s", entry.getKey(), entry.getValue().toJson()));
                if (i++ < current.size() - 1) json.append(",");
            }
            
            json.append("},\"history\":{");
            
            i = 0;
            for (Map.Entry<String, List<FileEvent>> entry : history.entrySet()) {
                json.append(String.format("\"%s\":[", entry.getKey()));
                for (int j = 0; j < entry.getValue().size(); j++) {
                    json.append(entry.getValue().get(j).toJson());
                    if (j < entry.getValue().size() - 1) json.append(",");
                }
                json.append("]");
                if (i++ < history.size() - 1) json.append(",");
            }
            
            json.append("},\"deleted\":{");
            
            i = 0;
            for (Map.Entry<String, DeletedFile> entry : deleted.entrySet()) {
                json.append(String.format("\"%s\":%s", entry.getKey(), entry.getValue().toJson()));
                if (i++ < deleted.size() - 1) json.append(",");
            }
            
            json.append("}}");
            return json.toString();
        }
        
        public String toText() {
            StringBuilder text = new StringBuilder();
            
            text.append("\n[Current Files]\n");
            for (Map.Entry<String, CurrentFile> entry : current.entrySet()) {
                text.append(String.format("  %s (%d bytes)\n", entry.getKey(), entry.getValue().size));
            }
            
            text.append("\n[Deleted Files]\n");
            for (Map.Entry<String, DeletedFile> entry : deleted.entrySet()) {
                text.append(String.format("  %s (deleted in %s)\n", 
                    entry.getKey(), entry.getValue().deletedIn));
            }
            
            return text.toString();
        }
    }
    
    public static class CurrentFile {
        public boolean exists;
        public long size;
        public String lastModified;
        
        public String toJson() {
            return String.format("{\"exists\":%b,\"size\":%d,\"lastModified\":\"%s\"}",
                exists, size, lastModified);
        }
    }
    
    public static class FileEvent {
        public String commit;
        public String action; // CREATED, MODIFIED, DELETED
        public String date;
        
        public String toJson() {
            return String.format("{\"commit\":\"%s\",\"action\":\"%s\",\"date\":\"%s\"}",
                commit, action, date);
        }
    }
    
    public static class DeletedFile {
        public String deletedIn;
        public String deletedDate;
        public String existedFor;
        
        public String toJson() {
            return String.format("{\"deletedIn\":\"%s\",\"deletedDate\":\"%s\",\"existedFor\":\"%s\"}",
                deletedIn, deletedDate, existedFor);
        }
    }
    
    public static class BranchInfo {
        public List<String> all;
        public List<String> merged;
        public List<String> active;
        
        public BranchInfo() {
            all = new ArrayList<>();
            merged = new ArrayList<>();
            active = new ArrayList<>();
        }
        
        public String toJson() {
            return String.format("{\"all\":%s,\"merged\":%s,\"active\":%s}",
                listToJson(all), listToJson(merged), listToJson(active));
        }
        
        public String toText() {
            StringBuilder text = new StringBuilder();
            text.append("All Branches: ").append(String.join(", ", all)).append("\n");
            text.append("Merged: ").append(String.join(", ", merged)).append("\n");
            text.append("Active: ").append(String.join(", ", active));
            return text.toString();
        }
        
        private String listToJson(List<String> list) {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                json.append("\"").append(list.get(i)).append("\"");
                if (i < list.size() - 1) json.append(",");
            }
            json.append("]");
            return json.toString();
        }
    }
    
    public static class Statistics {
        public int totalFiles;
        public int activeFiles;
        public int deletedFiles;
        public int totalCommits;
        public int contributors;
        
        public String toJson() {
            return String.format("{\"totalFiles\":%d,\"activeFiles\":%d,\"deletedFiles\":%d,\"totalCommits\":%d,\"contributors\":%d}",
                totalFiles, activeFiles, deletedFiles, totalCommits, contributors);
        }
        
        public String toText() {
            return String.format(
                "Total Files: %d (%d active, %d deleted)\n" +
                "Total Commits: %d\n" +
                "Contributors: %d",
                totalFiles, activeFiles, deletedFiles, totalCommits, contributors);
        }
    }
}
