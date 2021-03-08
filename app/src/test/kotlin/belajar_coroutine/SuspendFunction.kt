package belajar_coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class SuspendFunction {

    suspend fun hello(){
        println("hello")
        delay(2_000)
        println("world")
    }

    @Test
    fun suspendFunctionTest(){
        runBlocking {
            hello()
        }
    }


}