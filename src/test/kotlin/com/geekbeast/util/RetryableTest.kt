package com.geekbeast.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The retry loop previously slept after the final failure (delaying a terminal error by a full
 * backoff interval) and accepted maxAttempts = 0, which reached `lastException!!` with null.
 */
class RetryableTest {

    private fun noDelay() = RetryStrategy({ 0L }, 0L)

    @Test
    fun zeroAttemptsIsRejectedInsteadOfThrowingNpe() {
        assertThrows(IllegalArgumentException::class.java) {
            Unit.attempt(noDelay(), 0) { error("never runs") }
        }
    }

    @Test
    fun noBackoffFollowsTheFinalFailure() {
        // A five second backoff must not be paid when no further attempt will be made.
        val slow = RetryStrategy({ it }, 5_000L)
        var attempts = 0
        val elapsed = System.nanoTime().let { start ->
            assertThrows(RetryableCallFailedException::class.java) {
                Unit.attempt(slow, 1) {
                    attempts++
                    error("boom")
                }
            }
            (System.nanoTime() - start) / 1_000_000
        }
        assertEquals(1, attempts)
        assertTrue("terminal failure waited ${elapsed}ms for a retry that never happens", elapsed < 1_000)
    }

    @Test
    fun everyAttemptRunsBeforeFailing() {
        var attempts = 0
        assertThrows(RetryableCallFailedException::class.java) {
            Unit.attempt(noDelay(), 3) {
                attempts++
                error("boom")
            }
        }
        assertEquals(3, attempts)
    }

    @Test
    fun aSuccessfulAttemptReturnsItsValue() {
        assertEquals("ok", Unit.attempt(noDelay(), 3) { "ok" })
    }
}
