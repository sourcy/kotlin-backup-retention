package io.sourcy.retention

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File

class RetentionLogicTest : AbstractBaseTest() {

    private val retentionLogic = RetentionLogic(dryRunArgumentsAnd(emptyArray()), settings)

    @Test
    fun testKeepTodaysDaily() {
        val result = retentionLogic.calculateRetentionInfo(File("some_2018-01-18_05-07-59.tar.gz"))
        when (result) {
            is RetentionInfo.Info -> assertThat(result)
                    .matches { it.isDaily && !it.isExpired }
        }
    }

    @Test
    fun testKeepSevenDailys() {
        val result = retentionLogic.calculateRetentionInfo(File("some_2018-01-12_05-07-59.tar.gz"))
        when (result) {
            is RetentionInfo.Info -> assertThat(result)
                    .matches { it.isDaily && !it.isExpired }
        }
    }

    @Test
    fun testExpireEighthsDaily() {
        val result = retentionLogic.calculateRetentionInfo(File("some_2018-01-11_05-07-59.tar.gz"))
        when (result) {
            is RetentionInfo.Info -> assertThat(result)
                    .matches { it.isDaily && it.isExpired }
        }
    }

}