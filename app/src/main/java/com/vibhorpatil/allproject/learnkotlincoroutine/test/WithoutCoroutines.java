package com.vibhorpatil.allproject.learnkotlincoroutine.test;

import android.util.Log;

public class WithoutCoroutines {

    public final String TAG = "WithoutCoroutines";

    void usingThread() {
        Log.d(TAG, "usingThread Start");
        doTaskUsingThread(new Listener() {
            @Override
            public void onComplete(String result) {
                Log.d(TAG, "usingThread onComplete");
            }
        });
        Log.d(TAG, "usingThread End");
    }

    void doTaskUsingThread(Listener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                listener.onComplete("data");
            }
        }).start();
    }

}

interface Listener {
    void onComplete(String result);
}
