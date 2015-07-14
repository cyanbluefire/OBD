package com.neoway.vehiclebeta1.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtils {
    Handler mHandler;
    
    public ToastUtils() {
        mHandler = new Handler(Looper.getMainLooper());
    }
    
    class MyRunnable implements Runnable {
        private String contentString = null;
        private int contentId = 0;
        private boolean isString = false;
        
        public MyRunnable(int contentId) {
            super();
            this.contentId = contentId;
            isString = false;
        }
        
        public MyRunnable(String contentString) {
            super();
            this.contentString = contentString;
            isString = true;
        }
        
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (isString) {
                Toast.makeText(MyApplication.getContext(), contentString,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MyApplication.getContext(), contentId,
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    
    public void showToast(String content) {
        mHandler.post(new MyRunnable(content));
    }
    
    public void showToast(int contentId) {
        mHandler.post(new MyRunnable(contentId));
    }
} 
