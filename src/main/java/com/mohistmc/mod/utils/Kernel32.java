package com.mohistmc.mod.utils;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;

public interface Kernel32 extends Library {
    Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

    // 获取当前进程句柄
    WinNT.HANDLE GetCurrentProcess();

    WinNT.HANDLE OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);

    // 设置进程工作集大小
    boolean SetProcessWorkingSetSize(
            WinNT.HANDLE hProcess,
            int dwMinimumWorkingSetSize,
            int dwMaximumWorkingSetSize
    );

    // 获取当前进程ID
    int GetCurrentProcessId();

    // 获取最后一次API调用的错误码
    int GetLastError();

    // 关闭句柄
    boolean CloseHandle(WinNT.HANDLE hObject);
}