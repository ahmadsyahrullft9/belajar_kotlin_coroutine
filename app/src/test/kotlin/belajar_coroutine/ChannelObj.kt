package belajar_coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.junit.jupiter.api.Test
import java.nio.channels.Channels
import java.util.*
import java.util.concurrent.Executors

class ChannelObj {

//channel berfungsi untuk mengirim data antar coroutine
//channel memiliki dua buah fungsi, antara lain send() utk kirim dan receive() utk menerima
//secara default channel hanya dapat menerima 1 data sehingga
//channel tidak dapat diisi data baru "send", apabila masih ada data didalamnya atau belum diambil "receive" oleh coroutine lain -> terjadi blocking
//channel juga tidak dapat diambil "receive" data apabila tidak ada data didalamnya -> terjadi blocking
//channel bersifat blocking yg artinya jika proses perintah(send/receive) belum berjalan, maka dia akan block thread parent

    @Test
    fun channelTest() {
        val channel = Channel<Int>()
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)
        val sender = scope.launch {
            println("send 1")
            channel.send(1)
            println("send 2")
            channel.send(2)
        }
        val receiver = scope.launch {
            println("receive ${channel.receive()}")
            println("receive ${channel.receive()}")
        }
        runBlocking {
            joinAll(sender, receiver)
            channel.close()
        }
    }

    @Test
    fun channelBufferTest() {
        //dengan fitur buffer, channel dapat menampung data lebih dari 1 atau
        //dapat disesuaikan pada parameter capacity:Int
        //dengan begitu, sending dapat terus dilakukan sampai capacity penuh, sambil menunggu proses receiving oleh coroutine receiver
        //saat capacity telah penuh, channel akan melakukan blocking
        val channel = Channel<Int>(capacity = Channel.UNLIMITED)
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)
        val sender = scope.launch {
            println("send 1")
            channel.send(1)
            println("send 2")
            channel.send(2)
        }
        val receiver = scope.launch {
            println("receive ${channel.receive()}")
            println("receive ${channel.receive()}")
        }
        runBlocking {
            joinAll(sender, receiver)
            channel.close()
        }
    }

    @Test
    fun channelBufferedOverflowTest() {
        //buffered overflow memungkinkan data baru masuk dapat menggantikan tempat dari data yg ada didalam channel
        // SUSPEND = menggantikan data lama/TERAKHIR MASUK dgn data baru masuk
        // DROP_OLDEST = menggantikan data lama PALING DEPAN/LAMA dgn data baru masuk
        // DROP_LATEST = menggantikan data lama PALING BARU dgn data baru masuk
        val channel = Channel<Int>(capacity = 10, onBufferOverflow = BufferOverflow.DROP_OLDEST)
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)
        val sender = scope.launch {
            repeat(100) {
                println("send $it")
                channel.send(it)
            }
        }
        val receiver = scope.launch {
            repeat(10) {
                println("receive ${channel.receive()}")
            }
        }
        runBlocking {
            joinAll(sender, receiver)
            channel.close()
        }
    }

    @Test
    fun channelUndeliveredElement() {
        //undeliveredelement menghasilkan data yg belum sempat diterima/receive namun channel sudah di tutup/close()
        //menghasilkan throw ClosedSendChannelException
        val channel = Channel<Int>(capacity = 10, onBufferOverflow = BufferOverflow.DROP_OLDEST) { undeliveredElement ->
            println("UndeliveredElement $undeliveredElement")
        }
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)
        val sender = scope.launch {
            channel.send(120)
            channel.send(230)
        }
        channel.close()
        runBlocking {
            sender.join()
        }
    }

    //SCOOPE FUNCTION CHANNEL

    @Test
    fun createChannelWithProduceTest() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)
        val receiver = scope.produce(capacity = Channel.RENDEZVOUS) {
            repeat(10) {
                send(it)
            }
        }
        val job = scope.launch {
            repeat(10) {
                println("receive ${receiver.receive()}")
            }
        }
        runBlocking { job.join() }
    }

    @Test
    fun createChannelWithActorModelTest() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)
        val sender = scope.actor<Int>(capacity = 10) {
            repeat(10) {
                println("receive ${receive()}")
            }
        }
        val job = scope.launch {
            repeat(10) {
                sender.send(it)
            }
        }
        runBlocking { job.join() }
    }

    @Test
    fun tickerFunctionTest() {
        //ticker merupakan function coroutine untuk menjalankan func send per"delayMillis"
        //dengan receiver mengembalikan obj Unit/Void
        val ticker = ticker(delayMillis = 1000)
        runBlocking {
            val job = launch {
                repeat(10) {
                    ticker.receive()
                    println(Date())
                }
            }
            job.join()
        }
    }

    //BROADCAST CHANNEL

    @Test
    fun broadcastChannelTest() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)

        //broadcastchannel memungkinkan mengirim data kepada satu channel
        val broadcastChannel = BroadcastChannel<Int>(capacity = 10)
        //dengan membuat receiver lebih dari satu
        val reveiverChannel1 = broadcastChannel.openSubscription()
        val reveiverChannel2 = broadcastChannel.openSubscription()

        val sender = scope.launch { repeat(10) { broadcastChannel.send(it) } }
        val receiver1 = scope.launch { repeat(10) { println("receiver1 ${reveiverChannel1.receive()}") } }
        val receiver2 = scope.launch { repeat(10) { println("receiver2 ${reveiverChannel2.receive()}") } }
        runBlocking {
            joinAll(sender, receiver1, receiver2)
        }
    }

    @Test
    fun createBroadcastChannelWithBroaccastTest() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)

        val broadcastChannel = scope.broadcast(capacity = 10) { repeat(10) { send(it) } }
        val reveiverChannel1 = broadcastChannel.openSubscription()
        val reveiverChannel2 = broadcastChannel.openSubscription()

        val receiver1 = scope.launch { repeat(10) { println("receiver1 ${reveiverChannel1.receive()}") } }
        val receiver2 = scope.launch { repeat(10) { println("receiver2 ${reveiverChannel2.receive()}") } }
        runBlocking {
            joinAll(receiver1, receiver2)
        }
    }

    @Test
    fun conflatedBroadcastChannelTest() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)

        //conflatedBroadcastChannel memungkinkan sender mengirim data dan
        //apabila dalam channel masih terdapat data, channel otomatis akan mengganti/mereplace/memperbarui dengan data yg paling baru
        //akibatnya receiver hanya menerima data yg paling baru dari conflatedbroadcastchannel
        val conflatedBroadcastChannel = ConflatedBroadcastChannel<Int>()
        val receiveChannel = conflatedBroadcastChannel.openSubscription()

        val sender = scope.launch {
            repeat(10) {
                delay(1000)
                println("send $it")
                conflatedBroadcastChannel.send(it)
            }
        }
        val receiver = scope.launch {
            repeat(10) {
                delay(2000)
                println("receive ${receiveChannel.receive()}")
            }
        }

        runBlocking {
            delay(10000)
            scope.cancel()
        }
    }
}