/**
 * Git Utilities using isomorphic-git
 */

import git from 'isomorphic-git';
import http from 'isomorphic-git/http/web';
import { fsAdapter } from './fs-adapter';

export interface GitStatus {
  modified: string[];
  added: string[];
  deleted: string[];
  untracked: string[];
}

export interface CommitInfo {
  hash: string;
  shortHash: string;
  message: string;
  author: string;
  email: string;
  date: string;
}

export class GitUtils {
  private dir: string = '/';

  async init(dirHandle: FileSystemDirectoryHandle) {
    await fsAdapter.init(dirHandle);
    this.dir = '/';
  }

  async status(): Promise<GitStatus> {
    const result: GitStatus = {
      modified: [],
      added: [],
      deleted: [],
      untracked: [],
    };

    const statusMatrix = await git.statusMatrix({
      fs: fsAdapter as any,
      dir: this.dir,
    });

    for (const [filepath, headStatus, workdirStatus, stageStatus] of statusMatrix) {
      // Skip .git directory
      if (filepath.startsWith('.git/')) continue;

      // headStatus: 0 = absent, 1 = present
      // workdirStatus: 0 = absent, 1 = identical, 2 = modified
      // stageStatus: 0 = absent, 1 = identical, 2 = modified, 3 = added

      if (headStatus === 1 && workdirStatus === 2 && stageStatus === 1) {
        result.modified.push(filepath);
      } else if (headStatus === 0 && workdirStatus === 2 && stageStatus === 0) {
        result.untracked.push(filepath);
      } else if (headStatus === 0 && workdirStatus === 2 && stageStatus === 3) {
        result.added.push(filepath);
      } else if (headStatus === 1 && workdirStatus === 0 && stageStatus === 1) {
        result.deleted.push(filepath);
      } else if (headStatus === 1 && workdirStatus === 2 && stageStatus === 2) {
        result.modified.push(filepath);
      }
    }

    return result;
  }

  async add(filepath: string): Promise<void> {
    await git.add({
      fs: fsAdapter as any,
      dir: this.dir,
      filepath,
    });
  }

  async addAll(): Promise<void> {
    const status = await this.status();
    const allFiles = [
      ...status.modified,
      ...status.added,
      ...status.untracked,
    ];

    for (const file of allFiles) {
      await this.add(file);
    }
  }

  async commit(message: string, author?: { name: string; email: string }): Promise<string> {
    const authorInfo = author || {
      name: 'SuperGit User',
      email: 'user@supergit.local',
    };

    const sha = await git.commit({
      fs: fsAdapter as any,
      dir: this.dir,
      message,
      author: authorInfo,
    });

    return sha;
  }

  async log(count: number = 10): Promise<CommitInfo[]> {
    const commits = await git.log({
      fs: fsAdapter as any,
      dir: this.dir,
      depth: count,
    });

    return commits.map((commit) => ({
      hash: commit.oid,
      shortHash: commit.oid.substring(0, 7),
      message: commit.commit.message,
      author: commit.commit.author.name,
      email: commit.commit.author.email,
      date: new Date(commit.commit.author.timestamp * 1000).toISOString(),
    }));
  }

  async branches(): Promise<string[]> {
    const branches = await git.listBranches({
      fs: fsAdapter as any,
      dir: this.dir,
    });

    return branches;
  }

  async currentBranch(): Promise<string> {
    const branch = await git.currentBranch({
      fs: fsAdapter as any,
      dir: this.dir,
    });

    return branch || 'main';
  }

  async createBranch(name: string): Promise<void> {
    await git.branch({
      fs: fsAdapter as any,
      dir: this.dir,
      ref: name,
    });
  }

  async checkout(ref: string): Promise<void> {
    await git.checkout({
      fs: fsAdapter as any,
      dir: this.dir,
      ref,
    });
  }

  async deleteBranch(name: string): Promise<void> {
    await git.deleteBranch({
      fs: fsAdapter as any,
      dir: this.dir,
      ref: name,
    });
  }

  async initRepo(): Promise<void> {
    await git.init({
      fs: fsAdapter as any,
      dir: this.dir,
      defaultBranch: 'main',
    });
  }

  async clone(url: string, onProgress?: (progress: string) => void): Promise<void> {
    await git.clone({
      fs: fsAdapter as any,
      http,
      dir: this.dir,
      url,
      corsProxy: 'https://cors.isomorphic-git.org',
      onProgress: (event) => {
        if (onProgress) {
          onProgress(`${event.phase}: ${event.loaded}/${event.total}`);
        }
      },
    });
  }

  async fetch(remote: string = 'origin', onProgress?: (progress: string) => void): Promise<void> {
    await git.fetch({
      fs: fsAdapter as any,
      http,
      dir: this.dir,
      remote,
      corsProxy: 'https://cors.isomorphic-git.org',
      onProgress: (event) => {
        if (onProgress) {
          onProgress(`${event.phase}: ${event.loaded}/${event.total}`);
        }
      },
    });
  }

  async pull(remote: string = 'origin', onProgress?: (progress: string) => void): Promise<void> {
    await this.fetch(remote, onProgress);
    
    const currentBranch = await this.currentBranch();
    
    await git.merge({
      fs: fsAdapter as any,
      dir: this.dir,
      ours: currentBranch,
      theirs: `${remote}/${currentBranch}`,
    });
  }

  async push(remote: string = 'origin', onProgress?: (progress: string) => void): Promise<void> {
    const currentBranch = await this.currentBranch();
    
    await git.push({
      fs: fsAdapter as any,
      http,
      dir: this.dir,
      remote,
      ref: currentBranch,
      corsProxy: 'https://cors.isomorphic-git.org',
      onProgress: (event) => {
        if (onProgress) {
          onProgress(`${event.phase}: ${event.loaded}/${event.total}`);
        }
      },
    });
  }
}

export const gitUtils = new GitUtils();
