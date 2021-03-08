package belajar_coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.selects.select
import org.junit.jupiter.api.Test

class SelectFunctionTest {

    @Test
    fun selectDeferredTest() {
        //select function berfungsi mencari deferred mana yg paling cepat mengirimkan data
        val scope = CoroutineScope(Dispatchers.IO)
        val deferred1 = scope.async {
            delay(1000)
            1000
        }
        val deferred2 = scope.async {
            delay(2000)
            2000
        }
        val job = scope.launch {
            val win = select<Int> {
                deferred1.onAwait { it }
                deferred2.onAwait { it }
            }
            println("Win : $win")
        }
        runBlocking {
            job.join()
        }
    }

    @Test
    fun selectChannelTest() {
        //select function berfungsi mencari channel mana yg paling cepat mengirimkan data
        val scope = CoroutineScope(Dispatchers.IO)
        val receiveChannel1 = scope.produce<Int> {
            delay(1000)
            send(1000)
        }
        val receiveChannel2 = scope.produce<Int> {
            delay(2000)
            send(2000)
        }
        val job = scope.launch {
            val win = select<Int> {
                receiveChannel1.onReceive { it }
                receiveChannel2.onReceive { it }
            }
            println("Win : $win")
        }
        runBlocking {
            job.join()
        }
    }

    @Test
    fun selectChannelAndDeferredTest() {
        //dalam select function boleh diisi dengan deferred dan channel
        val scope = CoroutineScope(Dispatchers.IO)
        val deferred1 = scope.async {
            delay(1000)
            1000
        }
        val receiveChannel2 = scope.produce<Int> {
            delay(2000)
            send(2000)
        }
        val job = scope.launch {
            val win = select<Int> {
                deferred1.onAwait { it }
                receiveChannel2.onReceive { it }
            }
            println("Win : $win")
        }
        runBlocking {
            job.join()
        }
    }
}