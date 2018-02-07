package io.sourcy.retention

import arrow.core.Either
import arrow.core.getOrElse
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.opentest4j.TestAbortedException
import java.io.File
import java.time.format.DateTimeParseException

class RetentionLogicTest : AbstractBaseTest() {

    private val retentionLogic = RetentionLogic(buildArguments(dryTestRunArguments), testSettings)

    @Test
    fun testKeepCurrentDaily() {
        val result = retentionLogic.calculateFile(File("some_2018-01-18_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isDaily && !it.isExpired }
    }

    @Test
    fun testKeepSevenDailies() {
        val result = retentionLogic.calculateFile(File("some_2018-01-12_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isDaily && !it.isExpired }
    }

    @Test
    fun testExpireEighthDaily() {
        val result = retentionLogic.calculateFile(File("some_2018-01-11_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isDaily && it.isExpired }
    }

    @Test
    fun testKeepCurrentWeekly() {
        val result = retentionLogic.calculateFile(File("some_2018-01-14_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isWeekly && !it.isExpired }
    }

    @Test
    fun testKeepSevenWeeklies() {
        val result = retentionLogic.calculateFile(File("some_2017-11-26_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isWeekly && !it.isExpired }
    }

    @Test
    fun testExpireNinthWeekly() {
        val result = retentionLogic.calculateFile(File("some_2017-11-19_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isWeekly && it.isExpired }
    }

    @Test
    fun testKeepCurrentMonthly() {
        val result = retentionLogic.calculateFile(File("some_2018-01-01_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isMonthly && !it.isExpired }
    }

    @Test
    fun testKeepThirtySixMonthlies() {
        val result = retentionLogic.calculateFile(File("some_2015-02-01_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isMonthly && !it.isExpired }
    }

    @Test
    fun testExpireThirtySeventhMonthly() {
        val result = retentionLogic.calculateFile(File("some_2015-01-01_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isMonthly && it.isExpired }
    }

    @Test
    fun testKeepAllTrueRetentionFlags() {
        // first day of month and sunday
        val result = retentionLogic.calculateFile(File("some_2017-10-01_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isDaily && it.isWeekly && it.isMonthly && !it.isExpired }
    }

    @Test
    fun testExpireFarFutureWithAllTrueRetentionFlags() {
        // first day of month and sunday but far in the past thus expired
        val result = retentionLogic.calculateFile(File("some_2009-11-01_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isDaily && it.isWeekly && it.isMonthly && it.isExpired }
    }

    @Test
    fun testExpireNearFuture() {
        // first day of month and sunday but far in the past thus expired
        val result = retentionLogic.calculateFile(File("some_2017-12-28_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isDaily && !it.isWeekly && !it.isMonthly && it.isExpired }
    }

    @Test
    fun testInvalidDate() {
        // first day of month and sunday
        val result = retentionLogic.calculateFile(File("some_2017-10-99_05-07-59.tar.gz"))
        assertThatRetentionError(result)
                .isInstanceOf(DateTimeParseException::class.java)
    }

    @Test
    fun testNoDate() {
        // first day of month and sunday
        val result = retentionLogic.calculateFile(File("some.tar.gz"))
        assertThatRetentionError(result)
                .isInstanceOf(DateTimeParseException::class.java)
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