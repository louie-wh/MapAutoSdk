package com.baidu.mapautosdk.util;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: guangongbo
 * Date: 13-7-8
 * Time: 下午8:24
 */
public final class ProcessUtil {
    private static volatile int cachedMainProcessFlag = 0;

    /**
     * 判断当前进程是否为主进程
     *
     * @param context
     *
     * @return
     */
    public static synchronized boolean isMainProcess(Context context) {
        if (cachedMainProcessFlag == 0) {
            boolean isMain = getProcessName(context, android.os.Process.myPid()).equals(context.getPackageName());
            cachedMainProcessFlag = isMain ? 1 : -1;
        }
        return cachedMainProcessFlag > 0;
    }

    /**
     * 根据 pid 获取进程名称
     *
     * @param context
     * @param pid
     *
     * @return
     */
    public static String getProcessName(Context context, int pid) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
                activityManager.getRunningAppProcesses();
        try {
            for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
                if (info.pid == pid) {
                    return info.processName;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return "";
    }
}
