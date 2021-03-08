package belajar_coroutine

import org.junit.jupiter.api.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.system.measureTimeMillis

class Future {

    val executorService = Executors.newFixedThreadPool(4)

    fun getFoo(): Int {
        Thread.sleep(1000)
        return 10
    }

    fun getBar(): Int {
        Thread.sleep(1000)
        return 18
    }

    @Test
    fun nonFutureTest() {
        val time = measureTimeMillis {
            val result = getFoo() + getBar()
            println("Total result = $result")
        }
        println("Total time = $time")
    }

    @Test
    fun futureTest() {
        val time = measureTimeMillis {
            val foo: Future<Int> = executorService.submit(Callable { getFoo() })
            val bar: Future<Int> = executorService.submit(Callable { getBar() })
            val result = foo.get() + bar.get()
            println("Total result = $result")
        }
        println("Total time = $time")
    }
}