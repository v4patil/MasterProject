package com.vibhorpatil.allproject.learnkotlincoroutine.test

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WithCoroutines {

    companion object {
        const val TAG = "WithCoroutines"
    }

    // NOTE: NEVER USE GlobalScope, I have used it for the educational purpose only.
    fun usingCoroutines() {
        Log.d(TAG, "usingCoroutines Start")
        GlobalScope.launch {
            val result = doTaskUsingCoroutines()
            Log.d(TAG, "usingCoroutines onComplete")
        }
        Log.d(TAG, "usingCoroutines End")
    }

    private suspend fun doTaskUsingCoroutines(): String {
        delay(2000)
        return "data"
    }

    fun convertedCodeOfUsingCoroutines() {
        Log.d(TAG, "usingCoroutines Start")
        doTaskUsingCoroutines(object : Continuation {

            override fun resumeWith(result: String) {
                Log.d(TAG, "usingCoroutines onComplete")
            }

        })
        Log.d(TAG, "usingCoroutines End")
    }

    private fun doTaskUsingCoroutines(continuation: Continuation) {
        Thread {
            Thread.sleep(2000)
            continuation.resumeWith("data")
        }.start()
    }
}

fun interface Continuation {
    fun resumeWith(result: String)
}