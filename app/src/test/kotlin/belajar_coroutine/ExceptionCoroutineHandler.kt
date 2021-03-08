package belajar_coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.util.concurrent.Executors

class ExceptionCoroutineHandler {

    suspend fun getFoo(): Int {
        delay(1000)
        return 10
    }

    suspend fun getBar(): Int {
        delay(1000)
        return 10
    }

    @Test
    fun exceptionOnLaunch() {
        //exception yg berada pada coroutine dengan tipe launch akan di silent
        // karena launch tidak mengembalikan data seperti async
        runBlocking {
            val job = GlobalScope.launch {
                println("start job")
                throw IllegalArgumentException()
            }
            job.join()
            println("finish")
        }
    }

    @Test
    fun exceptionOnAsync() {
        //exception yg berada pada coroutine dengan tipe async TIDAK akan di silent
        // karena async harus mengembalikan data
        runBlocking {
            val deferred = GlobalScope.async {
                println("start job")
                throw IllegalArgumentException()
                getFoo()
            }
            try {
                deferred.await()
            } catch (error: IllegalArgumentException) {
                println("error")
            } finally {
                println("finish")
            }
        }
    }

    @Test
    fun exceptionHandlerTest() {
        //class ExceptionHandler hanya bekerja pada coroutine bertipe launch
        runBlocking {
            val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
                println("error ${throwable.message}")
            }
            val scope = CoroutineScope(exceptionHandler)
            val job = scope.launch {
                println("start job")
                throw IllegalArgumentException("persan error IllegalArgumentException")
            }
            job.join()
            println("finish")
        }
    }

    @Test
    fun exceptionHandlerParentChild() {
        //exception handler bekerja pada coroutine parent//
        runBlocking {
            val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
                println("exceptionHandler ${throwable.message}")
            }
            val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
            val scope = CoroutineScope(dispatcher)
            val job = scope.launch(exceptionHandler) {
                println("start parent")
                launch {
                    println("start child")
                    throw IllegalArgumentException("child error IllegalArgumentException")
                }
                throw IllegalArgumentException("parent error IllegalArgumentException")
            }
            job.join()
            println("finish")
        }
    }

    @Test
    fun exceptionHandlerSuperVisor() {
    //exception handler juga bekerja pada coroutine yg tepat berada di dalam supervisorscope namun tidak pada tingkat bawahnya//
        runBlocking {
            val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
                println("exceptionHandler ${throwable.message}")
            }
            val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
            val scope = CoroutineScope(dispatcher)
            val job = scope.launch(exceptionHandler) {
                println("start parent")
                supervisorScope {
                    launch(exceptionHandler) {
                        println("start child")
                        launch(exceptionHandler) {
                            println("start child01")
                            throw IllegalArgumentException("child01 error IllegalArgumentException")
                        }
                        throw IllegalArgumentException("child error IllegalArgumentException")
                    }
                }
                throw IllegalArgumentException("parent error IllegalArgumentException")
            }
            job.join()
            println("finish")
        }
    }
}