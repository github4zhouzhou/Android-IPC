// IBookManager.aidl
package com.v.ipc.aidl;

import com.v.ipc.aidl.Book;
import com.v.ipc.aidl.IOnNewBookArrivedListener;
// Declare any non-default types here with import statements

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void registerListener(IOnNewBookArrivedListener listener);
    void unregisterListener(IOnNewBookArrivedListener listener);
}
