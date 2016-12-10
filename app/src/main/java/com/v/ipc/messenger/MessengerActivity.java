package com.v.ipc.messenger;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.v.ipc.R;
import com.v.ipc.utils.MyUtils;

public class MessengerActivity extends Activity {

    private static final String TAG = "MessengerActivity";

    private Messenger mService;
    private Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());

    // 处理服务端发来的消息
    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MyUtils.MSG_FROM_SERVICE:
                Log.i(TAG, "receive msg from Service:" + msg.getData().getString("reply"));
                break;
            default:
                super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            // 用服务端的 binder 初始化 Messenger，就可以通过它向服务端发送消息了
            mService = new Messenger(service);
            Log.d(TAG, "bind service");
            Message msg = Message.obtain(null, MyUtils.MSG_FROM_CLIENT);
            Bundle data = new Bundle();
            data.putString("msg", "hello, this is client.");
            msg.setData(data);
            // 把客户端的 Messenger 传递给服务端，这样服务端就可以传回消息了
            msg.replyTo = mGetReplyMessenger;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        Intent intent = new Intent(MessengerActivity.this, MessengerService.class);
        intent.setAction("com.v.ipc.MessengerService.launch");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    
    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }
}
