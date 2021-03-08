package belajar_coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class DispatcherCoroutine {

    @Test
    fun dispatcherCoroutine() {
        runBlocking {
            println("runblocking : ${Thread.currentThread().name}")
            val job1 = GlobalScope.launch(Dispatchers.Default) {
                //menunjuk thread default
                println("job 1 : ${Thread.currentThread().name}")
            }
            val job2 = GlobalScope.launch(Dispatchers.IO) {
                //membentuk dan menunjuk thread otomatis sesuai jumlah core cpu
                println("job 2 : ${Thread.currentThread().name}")
            }
            joinAll(job1, job2)
        }
    }

    @Test
    fun confineUnConfine() {
        runBlocking {
            println("runblocking : ${Thread.currentThread().name}")
            val unconfine = GlobalScope.launch(Dispatchers.Unconfined) {
                //switch thread secara otomatis
                println("unconfine : ${Thread.currentThread().name}")
                delay(1000)
                println("unconfine : ${Thread.currentThread().name}")
                delay(1000)
                println("unconfine : ${Thread.currentThread().name}")
            }
            val confine = GlobalScope.launch {
                //menjalankan coroutine pada satu thread
                println("confine : ${Thread.currentThread().name}")
                delay(1000)
                println("confine : ${Thread.currentThread().name}")
                delay(1000)
                println("confine : ${Thread.currentThread().name}")
            }
            delay(3000)
        }
    }

    @Test
    fun executorServiceAsDispatcher() {
        //membuat custom dispatcher dengan executor service
        val dispatcherService = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
        val dispatcherWeb = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

        runBlocking {
            println("runblocking : ${Thread.currentThread().name}")
            val job1 = GlobalScope.launch(dispatcherService) {
                println("dispatcherService : ${Thread.currentThread().name}")
            }
            val job2 = GlobalScope.launch(dispatcherWeb) {
                println("dispatcherWeb : ${Thread.currentThread().name}")
            }
            joinAll(job1, job2)
        }
    }

    @Test
    fun withContextDispatcher(){
        //mengganti-ganti thread dalam coroutine
        val dispatcherService = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

        runBlocking {
            println("runblocking : ${Thread.currentThread().name}")
            val job = GlobalScope.launch(Dispatchers.IO) {
                println("1 : ${Thread.currentThread().name}")
                withContext(dispatcherService){
                    println("2 : ${Thread.currentThread().name}")
                }
                println("3 : ${Thread.currentThread().name}")
                withContext(dispatcherService){
                    println("4 : ${Thread.currentThread().name}")
                }
            }
            job.join()
        }
    }

    @Test
    fun nonCancelableDispatcher(){
        //membatalkan cancel coroutine yg sedang berjalan
        runBlocking {
            val job1 = GlobalScope.launch {
                try {
                    println("job1 start")
                    delay(1000)
                    println("job1 end")
                } finally {
                    println("job 1 isActive $isActive")
                    delay(1000)
                    println("job1 finally")
                }
            }
            val job2 = GlobalScope.launch {
                try {
                    println("job2 start")
                    delay(1000)
                    println("job2 end")
                } finally {
                    withContext(NonCancellable){
                        println("job2 isActive $isActive")
                        delay(1000)
                        println("job2 finally")
                    }
                }
            }
            job1.cancelAndJoin()
            job2.cancelAndJoin()
        }
    }
}