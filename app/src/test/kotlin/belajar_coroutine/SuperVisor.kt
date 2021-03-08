package belajar_coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.util.concurrent.Executors

class SuperVisor {

    @Test
    fun jobTest() {
        val dispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher + Job())
        //jika menggunakan Job class pada pembuatan parent, maka apabila
        // ada salah satu job yg failed maka akan memCANCEL semua job lain yg berada pada parent yg sama
        val job1 = scope.launch {
            delay(2000)
            println("job 1 ok")
        }
        val job2 = scope.launch {
            delay(1000)
            throw IllegalArgumentException("job 2 failed")
        }
        runBlocking {
            joinAll(job1, job2)
        }
    }

    @Test
    fun supervisorJobTest() {
        val dispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher + SupervisorJob())
        //jika menggunakan SupervisorJob class pada pembuatan parent, maka apabila
        // ada salah satu job yg failed maka TIDAK akan memCANCEL job lain yg berada pada parent yg sama
        val job1 = scope.launch {
            delay(2000)
            println("job 1 ok")
        }
        val job2 = scope.launch {
            delay(1000)
            throw IllegalArgumentException("job 2 failed")
        }
        runBlocking {
            joinAll(job1, job2)
        }
    }

    @Test
    fun supervisorScopeTest() {
        //"supervisorScope" berfungsi mengubah tipe job dari Job class menjadi SupervisorJob
        val dispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher + Job())

        runBlocking {
            supervisorScope {
                launch {
                    delay(2000)
                    println("job 1 ok")
                }
                launch {
                    delay(1000)
                    throw IllegalArgumentException("job 2 failed")
                }
            }
            delay(2000)
        }
    }
}