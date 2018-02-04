package io.sourcy.retention

import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

class RetentionLogicTest : AbstractBaseTest() {

    private val retentionLogic = RetentionLogic(dryTestRunArgumentsAnd(emptyArray()), settings)

    @Test
    fun testKeepCurrentDaily() {
        val result = retentionLogic.calculateRetentionInfo(File("some_2018-01-18_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isDaily && !it.isExpired }
    }

    @Test
    fun testKeepSevenDailies() {
        val result = retentionLogic.calculateRetentionInfo(File("some_2018-01-12_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isDaily && !it.isExpired }
    }

    @Test
    fun testExpireEighthDaily() {
        val result = retentionLogic.calculateRetentionInfo(File("some_2018-01-11_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isDaily && it.isExpired }
    }

    @Test
    fun testKeepCurrentWeekly() {
        val result = retentionLogic.calculateRetentionInfo(File("some_2018-01-14_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isWeekly && !it.isExpired }
    }

    @Test
    fun testKeepSevenWeeklies() {
        val result = retentionLogic.calculateRetentionInfo(File("some_2017-11-26_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isWeekly && !it.isExpired }
    }

    @Test
    fun testExpireNinthWeekly() {
        val result = retentionLogic.calculateRetentionInfo(File("some_2017-11-19_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isWeekly && it.isExpired }
    }

    @Test
    fun testKeepCurrentMonthly() {
        val result = retentionLogic.calculateRetentionInfo(File("some_2018-01-01_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isMonthly && !it.isExpired }
    }

    @Test
    fun testKeepThirtySixMonthlies() {
        val result = retentionLogic.calculateRetentionInfo(File("some_2015-02-01_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isMonthly && !it.isExpired }
    }

    @Test
    fun testExpireThirtySeventhMonthly() {
        val result = retentionLogic.calculateRetentionInfo(File("some_2015-01-01_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isMonthly && it.isExpired }
    }

    @Test
    fun testKeepAllTrueRetentionFlags() {
        // first day of month and sunday
        val result = retentionLogic.calculateRetentionInfo(File("some_2017-10-01_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isDaily && it.isWeekly && it.isMonthly && !it.isExpired }
    }

    @Test
    fun testExpireFarFutureWithAllTrueRetentionFlags() {
        // first day of month and sunday but far in the past thus expired
        val result = retentionLogic.calculateRetentionInfo(File("some_2009-11-01_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isDaily && it.isWeekly && it.isMonthly && it.isExpired }
    }

    @Test
    fun testExpireNearFuture() {
        // first day of month and sunday but far in the past thus expired
        val result = retentionLogic.calculateRetentionInfo(File("some_2017-12-28_05-07-59.tar.gz"))
        assertThatRetentionInfo(result)
                .matches { it.isDaily && !it.isWeekly && !it.isMonthly && it.isExpired }
    }

    private fun assertThatRetentionInfo(result: RetentionInfo): AbstractObjectAssert<*, RetentionInfo.Info> =
            when (result) {
                is RetentionInfo.Info -> assertThat(result)
                is RetentionInfo.Error -> throw result.exception
            }
}