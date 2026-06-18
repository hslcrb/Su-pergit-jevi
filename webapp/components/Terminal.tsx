'use client';

import { useEffect, useRef } from 'react';
import { Terminal as XTerm } from '@xterm/xterm';
import { FitAddon } from '@xterm/addon-fit';
import '@xterm/xterm/css/xterm.css';

interface TerminalProps {
  onCommand?: (command: string) => void;
  commands?: TerminalCommand[];
}

export interface TerminalCommand {
  command: string;
  output: string;
  type?: 'success' | 'error' | 'info' | 'warning';
}

export default function Terminal({ onCommand, commands }: TerminalProps) {
  const terminalRef = useRef<HTMLDivElement>(null);
  const xtermRef = useRef<XTerm | null>(null);
  const fitAddonRef = useRef<FitAddon | null>(null);
  const currentLineRef = useRef<string>('');

  useEffect(() => {
    if (!terminalRef.current) return;

    const xterm = new XTerm({
      cursorBlink: true,
      fontSize: 14,
      fontFamily: 'Menlo, Monaco, "Courier New", monospace',
      theme: {
        background: '#1e1e1e',
        foreground: '#d4d4d4',
        cursor: '#ffffff',
        black: '#000000',
        red: '#cd3131',
        green: '#0dbc79',
        yellow: '#e5e510',
        blue: '#2472c8',
        magenta: '#bc3fbc',
        cyan: '#11a8cd',
        white: '#e5e5e5',
        brightBlack: '#666666',
        brightRed: '#f14c4c',
        brightGreen: '#23d18b',
        brightYellow: '#f5f543',
        brightBlue: '#3b8eea',
        brightMagenta: '#d670d6',
        brightCyan: '#29b8db',
        brightWhite: '#e5e5e5',
      },
    });

    const fitAddon = new FitAddon();
    xterm.loadAddon(fitAddon);

    xterm.open(terminalRef.current);
    fitAddon.fit();

    xtermRef.current = xterm;
    fitAddonRef.current = fitAddon;

    // Welcome message
    xterm.writeln('\x1b[1;32m╔══════════════════════════════════════════════════════════╗\x1b[0m');
    xterm.writeln('\x1b[1;32m║       SuperGit-Jevi Web - 웹에서 실행되는 슈퍼깃         ║\x1b[0m');
    xterm.writeln('\x1b[1;32m╚══════════════════════════════════════════════════════════╝\x1b[0m');
    xterm.writeln('');
    xterm.writeln('폴더를 선택하여 시작하세요.');
    xterm.writeln('명령어를 입력하거나 메뉴 번호를 선택하세요.');
    xterm.writeln('');

    // Prompt
    const writePrompt = () => {
      xterm.write('\x1b[1;36msupergit>\x1b[0m ');
    };

    writePrompt();

    // Handle input
    xterm.onData((data) => {
      const code = data.charCodeAt(0);

      // Enter
      if (code === 13) {
        xterm.writeln('');
        if (currentLineRef.current.trim() && onCommand) {
          onCommand(currentLineRef.current.trim());
        }
        currentLineRef.current = '';
        writePrompt();
      }
      // Backspace
      else if (code === 127) {
        if (currentLineRef.current.length > 0) {
          currentLineRef.current = currentLineRef.current.slice(0, -1);
          xterm.write('\b \b');
        }
      }
      // Ctrl+C
      else if (code === 3) {
        xterm.writeln('^C');
        currentLineRef.current = '';
        writePrompt();
      }
      // Normal character
      else if (code >= 32 && code < 127) {
        currentLineRef.current += data;
        xterm.write(data);
      }
    });

    // Handle resize
    const handleResize = () => {
      fitAddon.fit();
    };

    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
      xterm.dispose();
    };
  }, [onCommand]);

  // Execute commands
  useEffect(() => {
    if (!commands || commands.length === 0 || !xtermRef.current) return;

    const xterm = xtermRef.current;
    const lastCommand = commands[commands.length - 1];

    const colors = {
      success: '\x1b[1;32m', // Green
      error: '\x1b[1;31m',   // Red
      info: '\x1b[1;36m',    // Cyan
      warning: '\x1b[1;33m', // Yellow
    };

    const color = colors[lastCommand.type || 'info'];
    const reset = '\x1b[0m';

    // Write output with color
    const lines = lastCommand.output.split('\n');
    lines.forEach((line) => {
      xterm.writeln(color + line + reset);
    });

    xterm.writeln('');
    xterm.write('\x1b[1;36msupergit>\x1b[0m ');
  }, [commands]);

  return (
    <div
      ref={terminalRef}
      className="w-full h-full bg-[#1e1e1e] rounded-lg overflow-hidden"
      style={{ minHeight: '400px' }}
    />
  );
}
