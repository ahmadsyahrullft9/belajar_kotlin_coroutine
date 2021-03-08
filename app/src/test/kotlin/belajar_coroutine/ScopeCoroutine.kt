package belajar_coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class ScopeCoroutine {

    //flow yg mengatur dan sebagai parent dari beberapa coroutine childnya
    val createScope = CoroutineScope(Dispatchers.IO)

    suspend fun getFoo(): Int {
        delay(1000)
        println("getFoo ${Thread.currentThread().name}")
        return 10
    }

    suspend fun getBar(): Int {
        delay(1000)
        println("getBar ${Thread.currentThread().name}")
        return 11
    }

    suspend fun getSum(): Int = coroutineScope {
        println("getSum ${Thread.currentThread().name}")
        val foo = async { getFoo() }
        val bar = async { getBar() }
        foo.await() + bar.await()
    }

    suspend fun getSumManual(): Int {
        println("getSumManual ${Thread.currentThread().name}")
        val scope = CoroutineScope(Dispatchers.IO)
        val foo = scope.async { getFoo() }
        val bar = scope.async { getBar() }
        return foo.await() + bar.await()
    }

    @Test
    fun scopeTest() {
        val job_1 = createScope.launch {
            delay(1000)
            println("job_1 = ${Thread.currentThread().name}")
        }
        val job_2 = createScope.launch {
            delay(2000)
            println("job_2 = ${Thread.currentThread().name}")
        }
        runBlocking {
            delay(2000)
            println("done")
        }
    }

    @Test
    fun cancelScopeTest() {
        val job_1 = createScope.launch {
            delay(1000)
            println("job_1 = ${Thread.currentThread().name}")
        }
        val job_2 = createScope.launch {
            delay(2000)
            println("job_2 = ${Thread.currentThread().name}")
        }
        runBlocking {
            delay(1000)
            createScope.cancel()
            delay(2000)
            println("done")
        }
    }

    @Test
    fun scopeFunctionTest() {
        val job = createScope.launch {
            val result = getSum()
            println("result = $result")
        }
        runBlocking {
            job.join()
        }
    }

    @Test
    fun parentChildScope() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val parentScope = CoroutineScope(dispatcher)
        val job = parentScope.launch {
            println("parentScope ${Thread.currentThread().name}")
            coroutineScope {
                launch {
                    delay(1000)
                    println("childScope ${Thread.currentThread().name}")
                }
            }
        }
        runBlocking { job.join() }
    }

    @Test
    fun cancelParentScope() {
        //membatalkan parent berarti membatalkan semua child
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val parentScope = CoroutineScope(dispatcher)
        val job = parentScope.launch {
            println("parentScope ${Thread.currentThread().name}")
            coroutineScope {
                launch {
                    delay(1000)
                    println("childScope ${Thread.currentThread().name}")
                }
            }
        }
        //runBlocking { job.cancelAndJoin() }
        runBlocking {
            delay(500)
            parentScope.cancel()
        }
    }
}