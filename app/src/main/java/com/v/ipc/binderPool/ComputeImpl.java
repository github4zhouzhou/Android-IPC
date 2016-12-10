package com.v.ipc.binderPool;

import android.os.RemoteException;

import com.v.ipc.aidl.binderPool.ICompute;

public class ComputeImpl extends ICompute.Stub {

    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }

}
