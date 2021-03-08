package belajar_coroutine

import org.junit.jupiter.api.Test
import java.util.*
import kotlin.concurrent.thread

class ThreadClass {

    @Test
    fun threadTest() {
        thread(start = true) {
            println("Done ${Thread.currentThread().name} ${Date()}")
            Thread.sleep(1000)
        }

        println("MENUNGGU")
        Thread.sleep(2000)
        println("SELESAI ${Date()}")
    }

    @Test
    fun multipleThreadTest() {
        thread(start = true) {
            println("Done-1 ${Thread.currentThread().name} ${Date()}")
            Thread.sleep(1000)
        }

        thread(start = true) {
            println("Done-2 ${Thread.currentThread().name} ${Date()}")
            Thread.sleep(1000)
        }

        println("MENUNGGU")
        Thread.sleep(2000)
        println("SELESAI ${Date()}")
    }
}