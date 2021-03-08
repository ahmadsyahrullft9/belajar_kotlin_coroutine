package belajar_coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class LockingDataCoroutine {

    val mutex = Mutex()
    //1 coroutine dapat mencegah coroutine lain untuk memproses data yg diproses mutex.locking

    val semaphore = Semaphore(permits = 2)
    //beberapa (sesuai jml permits) coroutine dapat mencegah coroutine lain untuk memproses data yg diproses semaphore.permit


    @Test
    fun lockingWithNoLocking() {
        var counter: Int = 0
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)
        repeat(100) {
            scope.launch {
                repeat(1000) {
                    counter++
                }
            }
        }
        runBlocking {
            delay(5000)
            println("counter = $counter")
        }
    }

    @Test
    fun lockingWithMuteX() {
        var counter: Int = 0
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)
        repeat(100) {
            scope.launch {
                repeat(1000) {
                    mutex.withLock {
                        counter++
                    }
                }
            }
        }
        runBlocking {
            delay(5000)
            println("counter = $counter")
        }
    }

    @Test
    fun lockingWithSemaphore() {
        var counter: Int = 0
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)
        repeat(100) {
            scope.launch {
                repeat(1000) {
                    semaphore.withPermit {
                        counter++
                    }
                }
            }
        }
        runBlocking {
            delay(5000)
            println("counter = $counter")
        }
    }
}