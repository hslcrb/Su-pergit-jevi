'use client';

import { useState } from 'react';
import Terminal, { TerminalCommand } from '@/components/Terminal';
import { gitUtils } from '@/lib/git-utils';

export default function Home() {
  const [dirHandle, setDirHandle] = useState<FileSystemDirectoryHandle | null>(null);
  const [commands, setCommands] = useState<TerminalCommand[]>([]);
  const [isProcessing, setIsProcessing] = useState(false);

  const addCommand = (command: string, output: string, type: TerminalCommand['type'] = 'info') => {
    setCommands((prev) => [...prev, { command, output, type }]);
  };

  const selectDirectory = async () => {
    try {
      // @ts-ignore - File System Access API
      const handle = await window.showDirectoryPicker({
        mode: 'readwrite',
      });

      setDirHandle(handle);
      await gitUtils.init(handle);

      addCommand(
        'select-folder',
        `[성공] 폴더 선택됨: ${handle.name}\n폴더가 준비되었습니다. 명령어를 입력하세요.`,
        'success'
      );
    } catch (error: any) {
      addCommand('select-folder', `[에러] 폴더 선택 실패: ${error.message}`, 'error');
    }
  };

  const handleCommand = async (input: string) => {
    if (isProcessing) {
      addCommand(input, '[경고] 이전 명령이 실행 중입니다...', 'warning');
      return;
    }

    if (!dirHandle && input !== 'select' && input !== 'h' && input !== 'help') {
      addCommand(input, '[경고] 먼저 폴더를 선택하세요. "select" 명령을 입력하세요.', 'warning');
      return;
    }

    setIsProcessing(true);

    try {
      const cmd = input.toLowerCase().trim();

      switch (cmd) {
        case 'select':
          await selectDirectory();
          break;

        case '1':
        case 'status':
          await handleStatus();
          break;

        case '2':
        case 'commit':
          addCommand(input, '[정보] 커밋 메시지를 입력하세요: commit <message>', 'info');
          break;

        case '3':
        case 'push':
          await handlePush();
          break;

        case '4':
        case 'branches':
          await handleBranches();
          break;

        case '5':
        case 'log':
        case 'history':
          await handleLog();
          break;

        case '6':
        case 'pull':
          await handlePull();
          break;

        case 'h':
        case 'help':
          showHelp();
          break;

        case 'init':
          await handleInit();
          break;

        case 'menu':
          showMenu();
          break;

        default:
          if (cmd.startsWith('commit ')) {
            const message = input.substring(7).trim();
            await handleCommit(message);
          } else if (cmd.startsWith('branch ')) {
            const branchName = input.substring(7).trim();
            await handleCreateBranch(branchName);
          } else if (cmd.startsWith('checkout ')) {
            const ref = input.substring(9).trim();
            await handleCheckout(ref);
          } else {
            addCommand(
              input,
              `[경고] 알 수 없는 명령: ${input}\n"help" 또는 "menu"를 입력하세요.`,
              'warning'
            );
          }
      }
    } catch (error: any) {
      addCommand(input, `[에러] ${error.message}`, 'error');
    } finally {
      setIsProcessing(false);
    }
  };

  const handleStatus = async () => {
    const status = await gitUtils.status();

    let output = '[상태 확인]\n\n';

    if (status.modified.length > 0) {
      output += `[수정됨] ${status.modified.length}개 파일:\n`;
      status.modified.forEach((file) => (output += `  - ${file}\n`));
      output += '\n';
    }

    if (status.added.length > 0) {
      output += `[추가됨] ${status.added.length}개 파일:\n`;
      status.added.forEach((file) => (output += `  + ${file}\n`));
      output += '\n';
    }

    if (status.deleted.length > 0) {
      output += `[삭제됨] ${status.deleted.length}개 파일:\n`;
      status.deleted.forEach((file) => (output += `  x ${file}\n`));
      output += '\n';
    }

    if (status.untracked.length > 0) {
      output += `[추적 안 됨] ${status.untracked.length}개 파일:\n`;
      status.untracked.forEach((file) => (output += `  ? ${file}\n`));
      output += '\n';
    }

    if (
      status.modified.length === 0 &&
      status.added.length === 0 &&
      status.deleted.length === 0 &&
      status.untracked.length === 0
    ) {
      output += '변경사항이 없습니다.\n';
    }

    addCommand('status', output, 'success');
  };

  const handleCommit = async (message: string) => {
    if (!message) {
      addCommand('commit', '[에러] 커밋 메시지를 입력하세요.', 'error');
      return;
    }

    await gitUtils.addAll();
    const sha = await gitUtils.commit(message);

    addCommand(
      `commit ${message}`,
      `[성공] 커밋 완료!\n커밋 해시: ${sha.substring(0, 7)}`,
      'success'
    );
  };

  const handlePush = async () => {
    let output = '[안전 모드 Push]\n\n';

    output += '[1/3] Fetch 중...\n';
    try {
      await gitUtils.fetch('origin', (progress) => {
        output += `  ${progress}\n`;
      });
      output += '[성공] Fetch 완료\n\n';
    } catch (e: any) {
      output += `[경고] Fetch 실패: ${e.message}\n\n`;
    }

    output += '[2/3] Pull 중...\n';
    try {
      await gitUtils.pull('origin', (progress) => {
        output += `  ${progress}\n`;
      });
      output += '[성공] Pull 완료\n\n';
    } catch (e: any) {
      output += `[경고] Pull 실패: ${e.message}\n\n`;
    }

    output += '[3/3] Push 중...\n';
    try {
      await gitUtils.push('origin', (progress) => {
        output += `  ${progress}\n`;
      });
      output += '[성공] Push 완료!\n';
      addCommand('push', output, 'success');
    } catch (e: any) {
      output += `[에러] Push 실패: ${e.message}\n`;
      addCommand('push', output, 'error');
    }
  };

  const handlePull = async () => {
    let output = '[Pull]\n\n';

    try {
      await gitUtils.pull('origin', (progress) => {
        output += `${progress}\n`;
      });
      output += '\n[성공] Pull 완료!';
      addCommand('pull', output, 'success');
    } catch (e: any) {
      output += `\n[에러] Pull 실패: ${e.message}`;
      addCommand('pull', output, 'error');
    }
  };

  const handleBranches = async () => {
    const branches = await gitUtils.branches();
    const current = await gitUtils.currentBranch();

    let output = '[브랜치 목록]\n\n';
    branches.forEach((branch) => {
      if (branch === current) {
        output += `* ${branch} (현재)\n`;
      } else {
        output += `  ${branch}\n`;
      }
    });

    addCommand('branches', output, 'success');
  };

  const handleLog = async () => {
    const commits = await gitUtils.log(10);

    let output = '[커밋 히스토리]\n\n';
    commits.forEach((commit) => {
      output += `[${commit.shortHash}] ${commit.message}\n`;
      output += `  작성자: ${commit.author} <${commit.email}>\n`;
      output += `  날짜: ${new Date(commit.date).toLocaleString()}\n\n`;
    });

    addCommand('log', output, 'success');
  };

  const handleInit = async () => {
    await gitUtils.initRepo();
    addCommand('init', '[성공] Git 저장소가 초기화되었습니다.', 'success');
  };

  const handleCreateBranch = async (name: string) => {
    await gitUtils.createBranch(name);
    addCommand(`branch ${name}`, `[성공] 브랜치 "${name}"이 생성되었습니다.`, 'success');
  };

  const handleCheckout = async (ref: string) => {
    await gitUtils.checkout(ref);
    addCommand(`checkout ${ref}`, `[성공] "${ref}"로 전환되었습니다.`, 'success');
  };

  const showHelp = () => {
    const help = `
[SuperGit-Jevi Web 도움말]

기본 명령어:
  select          - 폴더 선택 (시작 필수)
  menu            - 메뉴 표시
  
Git 명령어:
  1, status       - 상태 확인
  2, commit <msg> - 커밋 (예: commit "기능 추가")
  3, push         - 안전 모드 Push (자동 fetch+pull)
  4, branches     - 브랜치 목록
  5, log          - 커밋 히스토리
  6, pull         - Pull
  
  init            - Git 저장소 초기화
  branch <name>   - 브랜치 생성
  checkout <ref>  - 브랜치 전환

기타:
  h, help         - 이 도움말
  
브라우저 호환성:
  - Chrome/Edge 86+
  - Safari 15.2+
  - File System Access API 필요
`;

    addCommand('help', help, 'info');
  };

  const showMenu = () => {
    const menu = `
╔══════════════════════════════════════════════════════════╗
║              SuperGit-Jevi Web 메인 메뉴                 ║
╚══════════════════════════════════════════════════════════╝

[기본 기능]
  [1] 상태 확인 (status)
  [2] 커밋 (commit <message>)
  [3] Push (안전 모드)
  [4] 브랜치 관리 (branches)
  [5] 히스토리 (log)
  [6] Pull

[기타]
  select  - 폴더 선택
  init    - Git 저장소 초기화
  help    - 도움말

명령어 또는 번호를 입력하세요.
`;

    addCommand('menu', menu, 'info');
  };

  return (
    <div className="flex flex-col min-h-screen bg-gray-900">
      {/* Header */}
      <header className="bg-gray-800 border-b border-gray-700 px-6 py-4">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-white">SuperGit-Jevi Web</h1>
            <p className="text-sm text-gray-400 mt-1">
              제비처럼 빠른 웹 기반 Git 도구
            </p>
          </div>
          <button
            onClick={selectDirectory}
            className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
          >
            📁 폴더 선택
          </button>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 p-6">
        <div className="max-w-7xl mx-auto">
          {/* Status Bar */}
          <div className="mb-4 p-4 bg-gray-800 rounded-lg border border-gray-700">
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-2">
                <span className="text-gray-400">폴더:</span>
                <span className="text-white font-mono">
                  {dirHandle ? dirHandle.name : '선택 안 됨'}
                </span>
              </div>
              <div className="flex-1"></div>
              <div className="flex items-center gap-2">
                {isProcessing && (
                  <>
                    <div className="w-2 h-2 bg-yellow-500 rounded-full animate-pulse"></div>
                    <span className="text-yellow-500 text-sm">처리 중...</span>
                  </>
                )}
              </div>
            </div>
          </div>

          {/* Terminal */}
          <div className="bg-gray-800 rounded-lg border border-gray-700 p-4">
            <Terminal onCommand={handleCommand} commands={commands} />
          </div>

          {/* Quick Actions */}
          <div className="mt-4 grid grid-cols-2 md:grid-cols-4 gap-2">
            <button
              onClick={() => handleCommand('status')}
              disabled={!dirHandle || isProcessing}
              className="px-4 py-2 bg-gray-700 hover:bg-gray-600 text-white rounded disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              [1] Status
            </button>
            <button
              onClick={() => handleCommand('branches')}
              disabled={!dirHandle || isProcessing}
              className="px-4 py-2 bg-gray-700 hover:bg-gray-600 text-white rounded disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              [4] Branches
            </button>
            <button
              onClick={() => handleCommand('log')}
              disabled={!dirHandle || isProcessing}
              className="px-4 py-2 bg-gray-700 hover:bg-gray-600 text-white rounded disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              [5] Log
            </button>
            <button
              onClick={() => handleCommand('help')}
              className="px-4 py-2 bg-gray-700 hover:bg-gray-600 text-white rounded transition-colors"
            >
              Help
            </button>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-gray-800 border-t border-gray-700 px-6 py-4">
        <div className="max-w-7xl mx-auto text-center text-sm text-gray-400">
          SuperGit-Jevi Web - 브라우저에서 실행되는 슈퍼깃 | Chrome/Edge 86+ 또는 Safari 15.2+ 권장
        </div>
      </footer>
    </div>
  );
}
