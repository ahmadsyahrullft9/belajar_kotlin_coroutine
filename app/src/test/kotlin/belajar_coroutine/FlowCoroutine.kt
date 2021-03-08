package belajar_coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.Test
import java.util.*

class FlowCoroutine {

    @Test
    fun flowTest() {
        val flow1: Flow<Int> = flow {
            println("start flow")
            repeat(100) {
                println("send $it")
                emit(it)
            }
        }
        runBlocking {
            flow1.collect {
                println("receive $it")
            }
        }
    }

    suspend fun flowNumber(): Flow<Int> = flow {
        repeat(100) {
            emit(it)
        }
    }

    suspend fun changeNumber(number: Int): String {
        delay(100)
        return "Number $number"
    }

    @Test
    fun flowOperatorTest() {
        //operator yg ada pada flow merujuk pada operator yg trdapat pada kotlin collection
        runBlocking {
            flowNumber().filter { it % 2 == 0 }
                .map { changeNumber(it) }
                .collect { println(it) }
        }
    }

    @Test
    fun flowExceptionTest() {
        //exception dapat dipanggil pada function catch
        //error akan menghentikan proses flow
        runBlocking {
            flowNumber()
                .map { check(it != 20);it }
                .onEach { println(it) }
                .catch { println("Error.. ${it.message}") }
                .onCompletion { println("done") }
                .collect()
        }
    }

    @Test
    fun flowCancelable() {
        val scope = CoroutineScope(Dispatchers.IO)
        runBlocking {
            scope.launch {
                flowNumber()
                    .onEach {
                        //dengan mengCANCEL scope maka flow juga diCANCEL
                        if (it > 30) cancel()
                        else println(it)
                    }
                    .onCompletion { println("done") }
                    .collect()

            }
        }
    }

    @Test
    fun sharedFlowTest() {
        //shared flow merupakan jenis dari flow dimana penerima data nya bisa lebih dari satu
        //shared flow bertujuan untuk menggantikan/alternatif dari broadcastchannel
        val sharedFlow = MutableSharedFlow<Int>()
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            repeat(10) {
                println("sender $it | ${Date()}")
                sharedFlow.emit(it)
                delay(1000)
            }
        }
        scope.launch {
            sharedFlow.asSharedFlow()
                .buffer(10)
                .map { "receiver1 $it | ${Date()}" }
                .collect {
                    println(it)
                    delay(1000)
                }
        }
        scope.launch {
            sharedFlow.asSharedFlow()
                .buffer(10)
                .map { "receiver2 $it | ${Date()}" }
                .collect {
                    println(it)
                    delay(2000)
                }
        }
        runBlocking {
            delay(22_000)
            scope.cancel()
        }
    }

    @Test
    fun stateFlowTest() {
        //stateflow mirip seperti sharedflow, namun yg membedakan adalah
        //receiver dari stateflow hanya akan menerima data yg paling baru
        //ini mirip seperti consep conflated broadcast channel
        val stateFlow = MutableStateFlow(0)
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            repeat(10) {
                println("sender $it | ${Date()}")
                stateFlow.emit(it)
                delay(1000)
            }
        }
        scope.launch {
            stateFlow.asStateFlow()
                .map { "receiver $it | ${Date()}" }
                .collect {
                    println(it)
                    delay(2000)
                }
        }
        runBlocking {
            delay(22_000)
            scope.cancel()
        }
    }
}