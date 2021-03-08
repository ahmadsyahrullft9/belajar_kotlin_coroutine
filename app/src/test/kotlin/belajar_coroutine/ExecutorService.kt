package belajar_coroutine

import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.Executors

class ExecutorService {

    @Test
    fun singleExecutorServiceTest() {
        val executorService = Executors.newSingleThreadExecutor()
        repeat(10){
            val runnable = Runnable {
                Thread.sleep(1000)
                println("Done $it ${Thread.currentThread().name} ${Date()}")
            }
            executorService.execute(runnable)
        }

        println("MENUNGGU")
        Thread.sleep(11_000)
        println("SELESAI")
    }

    @Test
    fun fixedExecutorServiceTest() {
        val executorService = Executors.newFixedThreadPool(3)
        repeat(10){
            val runnable = Runnable {
                Thread.sleep(1000)
                println("Done $it ${Thread.currentThread().name} ${Date()}")
            }
            executorService.execute(runnable)
        }

        println("MENUNGGU")
        Thread.sleep(11_000)
        println("SELESAI")
    }

    @Test
    fun cachedExecutorServiceTest() {
        val executorService = Executors.newCachedThreadPool()
        repeat(10){
            val runnable = Runnable {
                Thread.sleep(1000)
                println("Done $it ${Thread.currentThread().name} ${Date()}")
            }
            executorService.execute(runnable)
        }

        println("MENUNGGU")
        Thread.sleep(11_000)
        println("SELESAI")
    }
}