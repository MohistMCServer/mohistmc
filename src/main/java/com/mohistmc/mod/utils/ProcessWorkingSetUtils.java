package com.mohistmc.mod.utils;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public class ProcessWorkingSetUtils {

    // 定义必要的权限常量
    public static final int PROCESS_SET_QUOTA = 0x0100;
    public static final int PROCESS_QUERY_INFORMATION = 0x0400;

    // 定义提权所需的常量和结构体
    public static final String SE_DEBUG_NAME = "SeDebugPrivilege";
    public static final int TOKEN_ADJUST_PRIVILEGES = 0x0020;
    public static final int TOKEN_QUERY = 0x0008;
    public static final WinDef.DWORD SE_PRIVILEGE_ENABLED = new WinDef.DWORD(0x00000002);

    // 启用调试权限
    private static boolean enableDebugPrivilege() {
        WinNT.HANDLEByReference hToken = new WinNT.HANDLEByReference();
        if (!Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(),
                TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, hToken)) {
            System.err.println("OpenProcessToken 失败");
            return false;
        }

        WinNT.LUID luid = new WinNT.LUID();
        if (!Advapi32.INSTANCE.LookupPrivilegeValue(null, SE_DEBUG_NAME, luid)) {
            System.err.println("LookupPrivilegeValue 失败");
            return false;
        }

        WinNT.TOKEN_PRIVILEGES newState = new WinNT.TOKEN_PRIVILEGES(1);
        newState.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES();
        newState.Privileges[0].Luid = luid;
        newState.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;

        if (!Advapi32.INSTANCE.AdjustTokenPrivileges(hToken.getValue(), false,
                newState, 0, null, null)) {
            System.err.println("AdjustTokenPrivileges 失败");
            return false;
        }

        return true;
    }

    public static void setProcessWorkingSetSize(int minSizeMB, int maxSizeMB) {
        // 提权
        if (!enableDebugPrivilege()) {
            System.err.println("提权失败");
            return;
        }

        // 使用OpenProcess获取当前进程的句柄并请求所需权限
        WinNT.HANDLE hProcess = Kernel32.INSTANCE.OpenProcess(
                PROCESS_SET_QUOTA | PROCESS_QUERY_INFORMATION,
                false,
                Kernel32.INSTANCE.GetCurrentProcessId() // 使用进程ID
        );

        if (hProcess == null) {
            System.err.println("OpenProcess 失败，错误代码：" + Kernel32.INSTANCE.GetLastError());
            return;
        }

        // 调用API设置工作集大小
        boolean success = Kernel32.INSTANCE.SetProcessWorkingSetSize(
                hProcess,
                minSizeMB,
                maxSizeMB
        );
        if (!success) {
            System.err.println("设置工作集大小失败！错误代码：" + Kernel32.INSTANCE.GetLastError());
        } else {
            System.out.println("设置工作集大小成功！");
        }

        // 关闭句柄
        //Kernel32.INSTANCE.CloseHandle(hProcess);
    }
}
