package net.cloudopt.next.core.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.cloudopt.next.core.Worker
import net.cloudopt.next.core.Worker.async
import net.cloudopt.next.core.Worker.await
import net.cloudopt.next.core.Worker.cancelTimer
import net.cloudopt.next.core.Worker.global
import net.cloudopt.next.core.Worker.setTimer
import net.cloudopt.next.core.Worker.then
import net.cloudopt.next.core.Worker.worker
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class TestWorker {
    @ExperimentalCoroutinesApi
    @BeforeTest
    fun init() {
        Dispatchers.setMain(Worker.dispatcher())
    }

    @ExperimentalCoroutinesApi
    @AfterTest
    fun clear() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAwaitOne() = runBlocking {
        val text = await {
            return@await "success"
        }
        assert(text == "success")
    }

    @Test
    fun testAwaitTwo() = runBlocking {
        val text = await<String> { promise ->
            promise.complete("success")
        }
        assert(text == "success")
    }

    @Test
    fun testAwaitInGlobalOne() {
        global {
            val text = await {
                return@await "success"
            }
            assert(text == "success")
        }
    }

    @Test
    fun testAwaitInGlobalTwo() {
        global {
            val text = await<String> { promise ->
                promise.complete("success")
            }
            assert(text == "success")
        }
    }

    @Test
    fun testAwaitInRunBlocking() {
        runBlocking {
            val text = await<String> { promise ->
                promise.complete("success")
            }
            assert(text == "success")
        }
    }

    @Test
    fun testAsync() {
        val text = async {
            return@async "success"
        }
        assert(text == "success")
    }

    @Test
    fun testAsyncFunction() {
        val text = asyncFunction()
        assert(text == "success")
    }

    private fun asyncFunction(): String = async {
        return@async await {
            return@await "success"
        }
    }

    @Test
    fun testWorker() {
        worker<String>({ promise ->
            promise.complete("cloudopt")
        }, { result ->
            assert(result.result() == "cloudopt")
        })
    }

    @Test
    fun testThen() {
        var i = 0
        then {
            i += 1
        }
        then {
            i += 2
        }

        then {
            i += 3
        }
        then {
            assert(i == 6)
        }
    }

    @Test
    fun testTimer() {
        assertDoesNotThrow {
            var i = 0
            var id: Long = -1
            setTimer(100, true) { timerId ->
                id = timerId
                i = i + 1
                if (i > 3) {
                    cancelTimer(id)
                }
            }
        }
    }


}