package com.v.ipc;

import android.app.Application;
import android.os.Process;
import android.util.Log;

import com.v.ipc.utils.MyUtils;

/**
 * Created by zhouzhou on 2016/11/28.
 * IPC Inter-Process Communication 进程间通信
 *
 * 一般来说多进程会造成如下几个方面的问题：
 * 1.静态成员和单例模式完全失效。
 * 2.线程同步机制完全失效。
 * 3.SharePreferences 的可靠性下降。
 * 4.Application 会多次创建。
 *
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = MyUtils.getProcessName(getApplicationContext(), Process.myPid());
        Log.d(TAG, "application start, process name: " + processName);
    }
}
