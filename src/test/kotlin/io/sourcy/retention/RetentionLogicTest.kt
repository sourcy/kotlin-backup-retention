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
            assertThatFileRetentionInfo(result)
                    .matches { it.isDaily && !it.isExpired }
        }

        @Test
        fun `keeps 7 dailies`() {
            val result = retentionLogic.calculateFile(File("some_2018-01-12_05-07-59.tar.gz"))
            assertThatFileRetentionInfo(result)
                    .matches { it.isDaily && !it.isExpired }
        }

        @Test
        fun `expires 8th daily`() {
            val result = retentionLogic.calculateFile(File("some_2018-01-11_05-07-59.tar.gz"))
            assertThatFileRetentionInfo(result)
                    .matches { it.isDaily && it.isExpired }
        }
    }

    @Nested
    inner class `weeklies calculation` {
        @Test
        fun `keeps current weekly`() {
            val result = retentionLogic.calculateFile(File("some_2018-01-14_05-07-59.tar.gz"))
            assertThatFileRetentionInfo(result)
                    .matches { it.isWeekly && !it.isExpired }
        }

        @Test
        fun `keeps 8 weeklies`() {
            val result = retentionLogic.calculateFile(File("some_2017-11-26_05-07-59.tar.gz"))
            assertThatFileRetentionInfo(result)
                    .matches { it.isWeekly && !it.isExpired }
        }

        @Test
        fun `expires 9th weekly`() {
            val result = retentionLogic.calculateFile(File("some_2017-11-19_05-07-59.tar.gz"))
            assertThatFileRetentionInfo(result)
                    .matches { it.isWeekly && it.isExpired }
        }
    }

    @Nested
    inner class `monthlies calculation` {
        @Test
        fun `keeps current monthly`() {
            val result = retentionLogic.calculateFile(File("some_2018-01-01_05-07-59.tar.gz"))
            assertThatFileRetentionInfo(result)
                    .matches { it.isMonthly && !it.isExpired }
        }

        @Test
        fun `keeps 36 monthlies`() {
            val result = retentionLogic.calculateFile(File("some_2015-02-01_05-07-59.tar.gz"))
            assertThatFileRetentionInfo(result)
                    .matches { it.isMonthly && !it.isExpired }
        }

        @Test
        fun `expires 37th monthly`() {
            val result = retentionLogic.calculateFile(File("some_2015-01-01_05-07-59.tar.gz"))
            assertThatFileRetentionInfo(result)
                    .matches { it.isMonthly && it.isExpired }
        }
    }

    @Nested
    inner class `expiry calculation with file in multiple retention sets (daily, weekly, monthly)` {
        @Test
        fun `near future + all sets = not expired`() {
            val result = retentionLogic.calculateFile(File("some_2017-10-01_05-07-59.tar.gz"))
            assertThatFileRetentionInfo(result)
                    .matches { it.isDaily && it.isWeekly && it.isMonthly && !it.isExpired }
        }

        @Test
        fun `far future + all sets = expired`() {
            val result = retentionLogic.calculateFile(File("some_2009-11-01_05-07-59.tar.gz"))
            assertThatFileRetentionInfo(result)
                    .matches { it.isDaily && it.isWeekly && it.isMonthly && it.isExpired }
        }

        @Test
        fun `near future + no set = expired`() {
            val result = retentionLogic.calculateFile(File("some_2017-12-28_05-07-59.tar.gz"))
            assertThatFileRetentionInfo(result)
                    .matches { it.isDaily && !it.isWeekly && !it.isMonthly && it.isExpired }
        }
    }

    @Nested
    inner class `date parser` {
        @Test
        fun `generates error on invalid date`() {
            val result = retentionLogic.calculateFile(File("some_2017-10-99_05-07-59.tar.gz"))
            assertThatFileRetentionError(result)
                    .isInstanceOf(DateTimeParseException::class.java)
        }

        @Test
        fun `generates error on missing date`() {
            val result = retentionLogic.calculateFile(File("some.tar.gz"))
            assertThatFileRetentionError(result)
                    .isInstanceOf(DateTimeParseException::class.java)
        }
    }

    @Test
    fun `keep minimum number of files in a directory`() {
        val expiredDirectory =
                List(15) { File("somedir/some_0001-01-${String.format("%02d", it + 1)}_some.tar.gz") }

        val result = retentionLogic.calculateDirectory(expiredDirectory)
        assertThatNumMinKeep(result).isEqualTo(10)
    }

    @Test
    fun `keep minimum number of files per directory`() {
        val expiredDirectories =
                List(15) { File("somedir/some_0001-01-${String.format("%02d", it + 1)}_some.tar.gz") } +
                        List(20) { File("somedir/subdir1/some_0001-01-${String.format("%02d", it + 1)}_some.tar.gz") } +
                        List(7) { File("somedir/subdir2/some_0001-01-${String.format("%02d", it + 1)}_some.tar.gz") }

        val result = retentionLogic.calculate(expiredDirectories)
        assertThatNumMinKeep(result).isEqualTo(27)
    }

    private fun assertThatFileRetentionInfo(result: Either<RetentionLogic.Error, RetentionLogic.FileOnlyInfo>): AbstractObjectAssert<*, RetentionLogic.FileOnlyInfo> =
            assertThat(getFileInfo(result))

    private fun assertThatFileRetentionError(result: Either<RetentionLogic.Error, RetentionLogic.FileOnlyInfo>): AbstractObjectAssert<*, out Throwable> =
            assertThat(getFileError(result).exception)

    private fun assertThatNumMinKeep(result: List<Either<RetentionLogic.Error, RetentionLogic.Info>>) =
            assertThat(result
                    .map(::getDirectoryInfo)
                    .filter { it.isMinKeep }
                    .filter { !it.isExpired }
                    .size
            )

    private fun getFileInfo(result: Either<RetentionLogic.Error, RetentionLogic.FileOnlyInfo>): RetentionLogic.FileOnlyInfo =
            result.getOrElse {
                throw TestAbortedException("RetentionInfo.Error $result instead of RetentionInfo.FileOnlyInfo")
            }

    private fun getDirectoryInfo(result: Either<RetentionLogic.Error, RetentionLogic.Info>): RetentionLogic.Info =
            result.getOrElse {
                throw TestAbortedException("RetentionInfo.Error $result instead of RetentionInfo.Info")
            }

    private fun getFileError(result: Either<RetentionLogic.Error, RetentionLogic.FileOnlyInfo>): RetentionLogic.Error {
        return result.swap().getOrElse {
            throw TestAbortedException("RetentionInfo.FileOnlyInfo $result instead of RetentionInfo.Error")
        }
    }
}