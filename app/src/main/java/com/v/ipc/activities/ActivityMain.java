package com.v.ipc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.v.ipc.R;
import com.v.ipc.aidl.BookManagerActivity;
import com.v.ipc.binderPool.BinderPoolActivity;
import com.v.ipc.manualBinder.Book;
import com.v.ipc.messenger.MessengerActivity;
import com.v.ipc.model.User;
import com.v.ipc.provider.ProviderActivity;
import com.v.ipc.socket.TCPClientActivity;
import com.v.ipc.utils.MyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ActivityMain extends Activity {
    private final static String TAG = ActivityMain.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // show id
        Log.d(TAG, "Utils id = " + MyUtils.sId);
        MyUtils.sId = MyUtils.sId + 1;

        Button btnSecond = (Button) findViewById(R.id.btn_show_second_act);
        Button btnThird = (Button) findViewById(R.id.btn_show_third_act);

        btnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityMain.this, ActivitySecond.class);
                User user = new User(0, "jake", true);
                user.book = new Book();
                intent.putExtra("extra_user", (Serializable) user);
                startActivity(intent);
            }
        });

        btnThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityMain.this, ActivityThird.class));
            }
        });


        Button btnMessenger = (Button) findViewById(R.id.btn_show_messenger_act);
        btnMessenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMain.this, MessengerActivity.class));
            }
        });

        Button btnAIDL = (Button) findViewById(R.id.btn_show_aidl_act);
        btnAIDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMain.this, BookManagerActivity.class));
            }
        });

        Button btnProvider = (Button) findViewById(R.id.btn_show_provider_act);
        btnProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMain.this, ProviderActivity.class));
            }
        });

        Button btnTCP = (Button) findViewById(R.id.btn_show_tcp_act);
        btnTCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMain.this, TCPClientActivity.class));
            }
        });

        Button btnBinderPool = (Button) findViewById(R.id.btn_show_bl_act);
        btnBinderPool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMain.this, BinderPoolActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        persistToFile();
        super.onStart();
    }

    /**
     * 使用文件共享的方式适合对数据同步不高的进程间进行通信，并且要妥善处理并发读/写的问题
     * 建议如果涉及到并发读写就不要用文件共享的方式了。
     * SharePreferences就是采用的文件共享的方式，不建议在进程间通信使用。
     */
    private void persistToFile() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                User user = new User(1, "hello world", false);
                File dir = new File(MyUtils.SHARE_FILE_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File cachedFile = new File(MyUtils.CACHE_FILE_PATH);

                ObjectOutputStream objectOutputStream = null;
                try {
                    objectOutputStream = new ObjectOutputStream(new FileOutputStream(cachedFile));
                    objectOutputStream.writeObject(user);
                    Log.d(TAG, "persist user:" + user);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    MyUtils.close(objectOutputStream);
                }
            }
        }).start();
    }
}
