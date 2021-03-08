package belajar_coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class SuspendCoroutine {

    suspend fun getFoo(): Int {
        delay(1000)
        return 10
    }

    suspend fun getBar(): Int {
        delay(1000)
        return 10
    }

    @Test
    fun suspendCoroutineTest() {
        runBlocking {
            val time = measureTimeMillis {
                val foo = GlobalScope.launch { getFoo() }
                val bar = GlobalScope.launch { getBar() }
                joinAll(foo, bar)
            }
            println("time : $time")
        }
    }

    @Test
    fun suspendAsyncCoroutineTest() {
        runBlocking {
            val time = measureTimeMillis {
                val foo = GlobalScope.async { getFoo() }
                val bar = GlobalScope.async { getBar() }
                val total = foo.await() + bar.await()
                println("total = $total")
                joinAll(foo, bar)
            }
            println("time : $time")
        }
    }

    @Test
    fun suspendAsyncAwaitAllCoroutineTest() {
        runBlocking {
            val time = measureTimeMillis {
                val foo = GlobalScope.async { getFoo() }
                val bar = GlobalScope.async { getBar() }
                val total = awaitAll(foo, bar).sum()
                println("total = $total")
                joinAll(foo, bar)
            }
            println("time : $time")
        }
    }

    suspend fun runJob(number: Int) {
        println("start job $number at ${Thread.currentThread().name}")
        yield() //yield mempersilahkann antar coroutine berbagi dispatcher satu sama lain jika memungkinkan
        println("end job $number at ${Thread.currentThread().name}")
    }

    @Test
    fun suspenYieldCoroutine() {
        runBlocking {
            GlobalScope.launch { runJob(2) }
            GlobalScope.launch { runJob(3) }
            delay(1000)
        }
    }
}