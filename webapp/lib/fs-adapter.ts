/**
 * File System Adapter for isomorphic-git
 * Uses browser's File System Access API
 */

export class FSAdapter {
  private dirHandle: FileSystemDirectoryHandle | null = null;
  private cache: Map<string, any> = new Map();

  async init(dirHandle: FileSystemDirectoryHandle) {
    this.dirHandle = dirHandle;
    this.cache.clear();
  }

  async readFile(filepath: string, options?: { encoding?: string }): Promise<string | Uint8Array> {
    if (!this.dirHandle) throw new Error('File system not initialized');
    
    const fileHandle = await this.getFileHandle(filepath);
    const file = await fileHandle.getFile();
    
    if (options?.encoding === 'utf8') {
      return await file.text();
    }
    
    const buffer = await file.arrayBuffer();
    return new Uint8Array(buffer);
  }

  async writeFile(filepath: string, data: string | Uint8Array): Promise<void> {
    if (!this.dirHandle) throw new Error('File system not initialized');
    
    const fileHandle = await this.getFileHandle(filepath, true);
    const writable = await fileHandle.createWritable();
    
    await writable.write(data as any);
    await writable.close();
  }

  async unlink(filepath: string): Promise<void> {
    if (!this.dirHandle) throw new Error('File system not initialized');
    
    const parts = filepath.split('/').filter(p => p);
    const fileName = parts.pop()!;
    const dirHandle = await this.getDirectoryHandle(parts);
    
    await dirHandle.removeEntry(fileName);
  }

  async readdir(filepath: string): Promise<string[]> {
    if (!this.dirHandle) throw new Error('File system not initialized');
    
    const parts = filepath.split('/').filter(p => p);
    const dirHandle = parts.length > 0 ? await this.getDirectoryHandle(parts) : this.dirHandle;
    
    const entries: string[] = [];
    // @ts-ignore
    for await (const entry of dirHandle.values()) {
      entries.push(entry.name);
    }
    
    return entries;
  }

  async mkdir(filepath: string): Promise<void> {
    if (!this.dirHandle) throw new Error('File system not initialized');
    
    const parts = filepath.split('/').filter(p => p);
    await this.getDirectoryHandle(parts, true);
  }

  async rmdir(filepath: string): Promise<void> {
    if (!this.dirHandle) throw new Error('File system not initialized');
    
    const parts = filepath.split('/').filter(p => p);
    const dirName = parts.pop()!;
    const parentHandle = parts.length > 0 ? await this.getDirectoryHandle(parts) : this.dirHandle;
    
    await parentHandle.removeEntry(dirName, { recursive: true });
  }

  async stat(filepath: string): Promise<{ type: string; mode: number; size: number; mtimeMs: number }> {
    if (!this.dirHandle) throw new Error('File system not initialized');
    
    try {
      const fileHandle = await this.getFileHandle(filepath);
      const file = await fileHandle.getFile();
      
      return {
        type: 'file',
        mode: 0o666,
        size: file.size,
        mtimeMs: file.lastModified,
      };
    } catch (e) {
      // Try as directory
      const parts = filepath.split('/').filter(p => p);
      await this.getDirectoryHandle(parts);
      
      return {
        type: 'dir',
        mode: 0o777,
        size: 0,
        mtimeMs: Date.now(),
      };
    }
  }

  async lstat(filepath: string) {
    return this.stat(filepath);
  }

  async readlink(filepath: string): Promise<string> {
    throw new Error('Symlinks not supported in browser');
  }

  async symlink(target: string, filepath: string): Promise<void> {
    throw new Error('Symlinks not supported in browser');
  }

  private async getFileHandle(filepath: string, create = false): Promise<FileSystemFileHandle> {
    const parts = filepath.split('/').filter(p => p);
    const fileName = parts.pop()!;
    
    let dirHandle = this.dirHandle!;
    
    for (const part of parts) {
      dirHandle = await dirHandle.getDirectoryHandle(part, { create });
    }
    
    return await dirHandle.getFileHandle(fileName, { create });
  }

  private async getDirectoryHandle(
    parts: string[],
    create = false
  ): Promise<FileSystemDirectoryHandle> {
    let dirHandle = this.dirHandle!;
    
    for (const part of parts) {
      dirHandle = await dirHandle.getDirectoryHandle(part, { create });
    }
    
    return dirHandle;
  }
}

export const fsAdapter = new FSAdapter();
