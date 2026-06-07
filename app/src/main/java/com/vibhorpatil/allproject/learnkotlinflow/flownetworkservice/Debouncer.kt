package com.vibhorpatil.allproject.learnkotlinflow.flownetworkservice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// How to Implement Debounce Using Coroutines?

class Debouncer(private val scope: CoroutineScope, private val waitTime: Long) {

    private var job: Job? = null

    fun post(block: () -> Unit) {
        job?.cancel()
        job = scope.launch {
            delay(waitTime)
            block()
        }
    }
}

fun main() = runBlocking {
    val debouncer = Debouncer(scope = this, waitTime = 300L)
    debouncer.post { println("MasterProject 1") }
    delay(100)
    debouncer.post { println("MasterProject 2") }
    delay(400)
    debouncer.post { println("MasterProject 3") }
    delay(200)
    debouncer.post { println("MasterProject 4") }
    delay(500)
}

/*
Output:
MasterProject 2
MasterProject 4
*/