package com.v.ipc.aidl.binderPool;

interface ISecurityCenter {
    String encrypt(String content);
    String decrypt(String password);
}