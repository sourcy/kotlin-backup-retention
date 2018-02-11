package io.sourcy.retention

import arrow.syntax.either.right
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate

class RetentionRunnerTest : AbstractBaseTest() {

    @Test
    fun `fail if max delete threshold is exceeded`() {
        val fiftyPercentToDelete = (1..5).map { fileToDelete() } + (1..5).map { fileToKeep() }
        val retentionRunner = RetentionRunner(buildArguments(defaultTestArguments), testSettings)

        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { retentionRunner.run(fiftyPercentToDelete) }
    }

    @Test
    fun `succeed if max delete threshold is exceeded but --force is supplied`() {
        val fiftyPercentToDelete = (1..5).map { fileToDelete() } + (1..5).map { fileToKeep() }
        val retentionRunner = RetentionRunner(buildArguments(defaultTestArguments + arrayOf("--force")), testSettings)

        retentionRunner.run(fiftyPercentToDelete)
    }

    private fun fileToDelete() =
            RetentionLogic.Info(RetentionLogic.FileOnlyInfo(File(""), LocalDate.now(), false, false, false, false), false).right()

    private fun fileToKeep() =
            RetentionLogic.Info(RetentionLogic.FileOnlyInfo(File(""), LocalDate.now(), false, false, false, true), false).right()

}