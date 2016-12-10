// IOnNewBookArrivedListener.aidl
package com.v.ipc.aidl;

import com.v.ipc.aidl.Book;
// Declare any non-default types here with import statements

interface IOnNewBookArrivedListener {
   void onNewBookArrived(in Book newBook);
}
