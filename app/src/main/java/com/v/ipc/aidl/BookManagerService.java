package com.v.ipc.aidl;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {

    private static final String TAG = "BMS";
    private final String permissionName = "com.v.ipc.permission.ACCESS_BOOK_SERVICE";

    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);

    /**
     * AIDL 内部还是把CopyOnWriteArrayList 当list使用，
     * 但是CopyOnWriteArrayList支持并发读/写，AIDL方法是在服务端的Binder线程池中执行的，
     * 因此当多个客户端同时连接时，会存在线程同步问题
     */
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();

    /**
     * 支持跨进程注册和反注册。
     * 客户端调用 IBookManager 的接口的注册和反注册时是跨进程的，注册和反注册方法都是在服务端实现的。
     * 虽然客户端注册和解注册时传递的是一个对象，但是服务端这边是根据客户端对象重新生成的，
     * 因为跨进程就是一个反序列化过程，所以服务端这边注册和解注册的对象必然是不一样的。
     * RemoteCallbackList 内部存的是 Binder 对象。
     * 虽然多次跨进程传输客户端的同一个对象会在服务端生成不同的对象，但是它们底层的 Binder 对象是同一个
     */
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<IOnNewBookArrivedListener>();

    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            SystemClock.sleep(5000);
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {

            // 权限校验
            int check = checkCallingOrSelfPermission(permissionName);
            Log.d(TAG, "check=" + check);
            if (check == PackageManager.PERMISSION_DENIED) {
                return false;
            }

            // 包名校验，可以进一步控制可以使用服务的应用的范围
            String packageName = null;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length > 0) {
                packageName = packages[0];
            }
            Log.d(TAG, "onTransact: " + packageName);
            if (!packageName.startsWith("com.v")) {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener)
                throws RemoteException {
            mListenerList.register(listener);

            final int N = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.d(TAG, "registerListener, current size:" + N);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener)
                throws RemoteException {
            boolean success = mListenerList.unregister(listener);

            if (success) {
                Log.d(TAG, "unregister success.");
            } else {
                Log.d(TAG, "not found, can not unregister.");
            }
            final int N = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.d(TAG, "unregisterListener, current size:" + N);
        };

    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "Ios"));
        // 模拟定时添加书籍
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 权限校验，如果没有声明权限禁止使用服务
        int check = checkCallingOrSelfPermission(permissionName);
        Log.d(TAG, "onBind check=" + check);
        if (check == PackageManager.PERMISSION_DENIED) {
            return null;
        }
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed.set(true);
        super.onDestroy();
    }

    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        final int N = mListenerList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener l = mListenerList.getBroadcastItem(i);
            if (l != null) {
                try {
                    l.onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mListenerList.finishBroadcast();
    }

    // 模拟每5s添加一本新书，然后通知客户端
    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            // do background processing here.....
            while (!mIsServiceDestoryed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId, "new book#" + bookId);
                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
