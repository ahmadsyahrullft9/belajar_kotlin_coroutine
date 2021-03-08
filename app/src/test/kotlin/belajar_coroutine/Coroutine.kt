package belajar_coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.*

class Coroutine {

    @Test
    fun coroutineTest() {
        runBlocking {
            GlobalScope.launch {
                println("start at ${Date()}")
                delay(3000)
                println("end at ${Date()}")
            }
            delay(4000)
        }
    }

    @Test
    fun lazyStartCoroutineTest() {
        runBlocking {
            val job: Job = GlobalScope.launch(start = CoroutineStart.LAZY) {
                println("start at ${Date()}")
                delay(3000)
                println("end at ${Date()}")
            }
            job.start()
            delay(4000)
        }
    }

    @Test
    fun joinJobcoroutineTest() {
        runBlocking {
            val job: Job = GlobalScope.launch {
                println("start at ${Date()}")
                delay(3000)
                println("end at ${Date()}")
            }
            job.join() // job dari courotine akan ditunggu oleh parent hingga selesai,
        }
    }

    @Test
    fun cancelCoroutineTest() {
        runBlocking {
            val job: Job = GlobalScope.launch {
                println("start at ${Date()}")
                delay(3000)
                println("end at ${Date()}")
            }
            job.cancelAndJoin()
            println("cancel at ${Date()}")
        }
    }

    @Test
    fun nonCancelableCoroutineTest() {
        runBlocking {
            val job: Job = GlobalScope.launch {
                println("start at ${Date()}")
                Thread.sleep(1000)
                println("end at ${Date()}")
            }
            job.cancelAndJoin()
        }
    }

    @Test
    fun checkIsCancelableCoroutineTest() {
        runBlocking {
            val job: Job = GlobalScope.launch {
                if (!isActive) throw CancellationException() //cara 1
                println("start at ${Date()}")

                Thread.sleep(1000)

                ensureActive()//cara 2
                println("end at ${Date()}")
            }
            job.cancelAndJoin()
        }
    }

    @Test
    fun cancelCoroutineFinallyTest() {
        runBlocking {
            val job: Job = GlobalScope.launch {
                try {
                    println("start at ${Date()}")
                    delay(2000)
                    println("end at ${Date()}")
                } finally {
                    println("finally")
                }
            }
            job.cancelAndJoin()
        }
    }

    @Test
    fun timeOutCoroutineTest() {
        runBlocking {
            val job = GlobalScope.launch {
                println("start at ${Date()}")
                withTimeout(5_000) {
                    repeat(100) {
                        println("it = $it")
                        delay(1000)
                    }
                }
                println("end at ${Date()}")
            }
            job.join()
        }
    }

    @Test
    fun timeOutNullCoroutineTest() {
        runBlocking {
            val job = GlobalScope.launch {
                println("start at ${Date()}")
                withTimeoutOrNull(5_000) {
                    repeat(100) {
                        println("it = $it")
                        delay(1000)
                    }
                }
                println("end at ${Date()}")
            }
            job.join()
        }
    }

    @Test
    fun parentChildCoroutineTest() {
        //parent akan selalu menunggu semua childnya selesai diproses
        runBlocking {
            val job = GlobalScope.launch {
                launch {
                    delay(2000)
                    println("child 1 done")
                }
                launch {
                    delay(3000)
                    println("child 2 done")
                }
                launch {
                    delay(4000)
                    println("child 3 done")
                }
                delay(1000)
                println("parent done")
            }
            //untuk membatalkan seluruh childnya saja
            //job.cancelChildren()

            job.join()
        }
    }

    @Test
    fun awaitCancelCoroutine(){
        //coroutine tidak akan berakhir sampai ada perintah cancel
        runBlocking {
            val job = launch {
                try {
                    println("start job at ${Date()}")
                    awaitCancellation()
                }finally {
                    println("cancel job at ${Date()}")
                }
            }
            delay(5000)
            job.cancelAndJoin()
        }
    }
}