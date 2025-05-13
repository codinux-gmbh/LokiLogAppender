package net.codinux.log.loki.web

/*      Copied from io.ktor.client.engine.java.JavaHttpResponseBodyHandler (io.ktor:ktor-client-java-jvm) under Apache License 2.0      */

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import java.io.*
import java.net.http.*
import java.nio.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.*

internal class JavaHttpResponseBodyHandler(
    private val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob(),
) : HttpResponse.BodyHandler<ByteArray> {

    override fun apply(responseInfo: HttpResponse.ResponseInfo): HttpResponse.BodySubscriber<ByteArray> {
        return JavaHttpResponseBodySubscriber(coroutineContext, responseInfo)
    }

    private class JavaHttpResponseBodySubscriber(
        callContext: CoroutineContext,
        response: HttpResponse.ResponseInfo,
    ) : HttpResponse.BodySubscriber<ByteArray>, CoroutineScope {

        private val consumerJob = Job(callContext[Job])
        override val coroutineContext: CoroutineContext = callContext + consumerJob

        var responseBytes: ByteArray? = null

        private val closed = AtomicBoolean(false)
        private val subscription = AtomicReference<Flow.Subscription?>(null)

        private val queue = Channel<ByteBuffer>(Channel.UNLIMITED)

        init {
            launch {
                try {
                    queue.consume {
                        while (isActive) {
                            var buffer = queue.tryReceive().getOrNull()
                            if (buffer == null) {
                                subscription.get()?.request(1)
                                buffer = queue.receive()
                            }

                            responseBytes = buffer.array()
                        }
                    }
                } catch (_: ClosedReceiveChannelException) {
                }
            }.apply {
                invokeOnCompletion {
                    consumerJob.complete()
                }
            }
        }

        override fun onSubscribe(s: Flow.Subscription) {
            try {
                if (!subscription.compareAndSet(null, s)) {
                    s.cancel()
                    return
                }

                // check whether the stream is already closed.
                // if so, we should cancel the subscription
                // immediately.
                if (closed.get()) {
                    s.cancel()
                } else {
                    s.request(1)
                }
            } catch (cause: Throwable) {
                try {
                    close(cause)
                } catch (ignored: IOException) {
                    // OK
                } finally {
                    onError(cause)
                }
            }
        }

        override fun onNext(items: List<ByteBuffer>) {
            items.forEach {
                if (it.hasRemaining()) {
                    queue.trySend(it).isSuccess
                }
            }
        }

        override fun onError(cause: Throwable) {
            close(cause)
        }

        override fun onComplete() {
            subscription.getAndSet(null)
            queue.close()
        }

        override fun getBody(): CompletionStage<ByteArray> {
            return CompletableFuture.completedStage(responseBytes)
        }

        private fun close(cause: Throwable) {
            if (!closed.compareAndSet(false, true)) {
                return
            }

            try {
                queue.close(cause)
                subscription.getAndSet(null)?.cancel()
            } finally {
                consumerJob.completeExceptionally(cause)
            }
        }
    }
}