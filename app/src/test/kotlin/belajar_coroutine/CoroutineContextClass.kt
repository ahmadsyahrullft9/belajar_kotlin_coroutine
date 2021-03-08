package belajar_coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class CoroutineContextClass {

    @Test
    fun coroutineContextTest() {
        runBlocking {
            val job = GlobalScope.launch(CoroutineName("testname")) {
                val ctx: CoroutineContext = coroutineContext
                println(ctx[Job])
                println(ctx[CoroutineName])
            }
            job.join()
        }
    }

    @Test
    fun coroutineContextPlusTest() {
        runBlocking {
            //operator "+" dapat menggabungkan coroutinecontext
            val scope = CoroutineScope(Dispatchers.IO + CoroutineName("scopename"))
            val job = scope.launch(CoroutineName("name1")) {
                println("parent run on ${Thread.currentThread().name}")
                withContext(Dispatchers.IO + CoroutineName("name2")) {
                    println("child run on ${Thread.currentThread().name}")
                }
            }
            job.join()
        }
    }
}