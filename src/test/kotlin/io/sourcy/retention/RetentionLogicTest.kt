package io.sourcy.retention

import arrow.core.Either
import arrow.core.getOrElse
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.opentest4j.TestAbortedException
import java.io.File
import java.time.format.DateTimeParseException

class RetentionLogicTest : AbstractBaseTest() {

    private val retentionLogic = RetentionLogic(buildArguments(dryTestRunArguments), testSettings)

    @Nested
    inner class `dailies calculation` {
        @Test
        fun `keeps current daily`() {
            val result = retentionLogic.calculateFile(File("some_2018-01-18_05-07-59.tar.gz"))
            assertThatRetentionInfo(result)
                    .matches { it.isDaily && !it.isExpired }
        }

        @Test
        fun `keeps 7 dailies`() {
            val result = retentionLogic.calculateFile(File("some_2018-01-12_05-07-59.tar.gz"))
            assertThatRetentionInfo(result)
                    .matches { it.isDaily && !it.isExpired }
        }

        @Test
        fun `expires 8th daily`() {
            val result = retentionLogic.calculateFile(File("some_2018-01-11_05-07-59.tar.gz"))
            assertThatRetentionInfo(result)
                    .matches { it.isDaily && it.isExpired }
        }
    }

    @Nested
    inner class `weeklies calculation` {
        @Test
        fun `keeps current weekly`() {
            val result = retentionLogic.calculateFile(File("some_2018-01-14_05-07-59.tar.gz"))
            assertThatRetentionInfo(result)
                    .matches { it.isWeekly && !it.isExpired }
        }

        @Test
        fun `keeps 8 weeklies`() {
            val result = retentionLogic.calculateFile(File("some_2017-11-26_05-07-59.tar.gz"))
            assertThatRetentionInfo(result)
                    .matches { it.isWeekly && !it.isExpired }
        }

        @Test
        fun `expires 9th weekly`() {
            val result = retentionLogic.calculateFile(File("some_2017-11-19_05-07-59.tar.gz"))
            assertThatRetentionInfo(result)
                    .matches { it.isWeekly && it.isExpired }
        }
    }

    @Nested
    inner class `monthlies calculation` {
        @Test
        fun `keeps current monthly`() {
            val result = retentionLogic.calculateFile(File("some_2018-01-01_05-07-59.tar.gz"))
            assertThatRetentionInfo(result)
                    .matches { it.isMonthly && !it.isExpired }
        }

        @Test
        fun `keeps 36 monthlies`() {
            val result = retentionLogic.calculateFile(File("some_2015-02-01_05-07-59.tar.gz"))
            assertThatRetentionInfo(result)
                    .matches { it.isMonthly && !it.isExpired }
        }

        @Test
        fun `expires 37th monthly`() {
            val result = retentionLogic.calculateFile(File("some_2015-01-01_05-07-59.tar.gz"))
            assertThatRetentionInfo(result)
                    .matches { it.isMonthly && it.isExpired }
        }
    }

    @Nested
    inner class `expiry calculation with file in multiple retention sets (daily, weekly, monthly)` {
        @Test
        fun `near future + all sets = not expired`  () {
            val result = retentionLogic.calculateFile(File("some_2017-10-01_05-07-59.tar.gz"))
            assertThatRetentionInfo(result)
                    .matches { it.isDaily && it.isWeekly && it.isMonthly && !it.isExpired }
        }

        @Test
        fun `far future + all sets = expired`() {
            val result = retentionLogic.calculateFile(File("some_2009-11-01_05-07-59.tar.gz"))
            assertThatRetentionInfo(result)
                    .matches { it.isDaily && it.isWeekly && it.isMonthly && it.isExpired }
        }

        @Test
        fun `near future + no set = expired`() {
            val result = retentionLogic.calculateFile(File("some_2017-12-28_05-07-59.tar.gz"))
            assertThatRetentionInfo(result)
                    .matches { it.isDaily && !it.isWeekly && !it.isMonthly && it.isExpired }
        }
    }

    @Nested
    inner class `date parser` {
        @Test
        fun `generates error on invalid date`() {
            val result = retentionLogic.calculateFile(File("some_2017-10-99_05-07-59.tar.gz"))
            assertThatRetentionError(result)
                    .isInstanceOf(DateTimeParseException::class.java)
        }

        @Test
        fun `generates error on missing date`() {
            val result = retentionLogic.calculateFile(File("some.tar.gz"))
            assertThatRetentionError(result)
                    .isInstanceOf(DateTimeParseException::class.java)
        }
    }

    @Nested
    inner class `keep minimum number of files per directory`  {
        // TODO
    }

    // TODO: tests for min keep and test for new calculate(files) method

    private fun assertThatRetentionInfo(result: Either<RetentionLogic.Error, RetentionLogic.FileOnlyInfo>): AbstractObjectAssert<*, RetentionLogic.FileOnlyInfo> =
            assertThat(assertInfo(result))

    private fun assertInfo(result: Either<RetentionLogic.Error, RetentionLogic.FileOnlyInfo>): RetentionLogic.FileOnlyInfo =
            result.getOrElse {
                throw TestAbortedException("RetentionInfo.Error $result instead of RetentionInfo.Info")
            }

    private fun assertThatRetentionError(result: Either<RetentionLogic.Error, RetentionLogic.FileOnlyInfo>): AbstractObjectAssert<*, out Throwable> =
            assertThat(assertError(result).exception)

    private fun assertError(result: Either<RetentionLogic.Error, RetentionLogic.FileOnlyInfo>): RetentionLogic.Error {
        return result.swap().getOrElse {
            throw TestAbortedException("RetentionInfo.Info $result instead of RetentionInfo.Error")
        }
    }
}