package com.v.ipc.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Environment;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public class MyUtils {
    public static int sId = 1;

    public static final String SHARE_FILE_PATH = Environment
            .getExternalStorageDirectory().getPath()
            + "/v/";

    public static final String CACHE_FILE_PATH = SHARE_FILE_PATH + "usercache";

    public static final int MSG_FROM_CLIENT = 0;
    public static final int MSG_FROM_SERVICE = 1;

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void executeInThread(Runnable runnable) {
        new Thread(runnable).start();
    }

}
