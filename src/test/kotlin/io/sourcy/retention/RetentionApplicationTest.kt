package io.sourcy.retention

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.format.DateTimeParseException

class RetentionApplicationTest : AbstractBaseTest() {
    @Test
    fun executesSuccessfullyDry() {
        RetentionApplication(testSettings).run(dryTestRunArgumentsAnd(arrayOf("--verbose")))
    }

    @Test
    fun executesSuccessfullyReal() {
        RetentionApplication(testSettings).run(testRunArgumentsAnd(arrayOf("--verbose")))
    }

    @Test
    fun failsOnException() {
        Assertions.assertThatExceptionOfType(DateTimeParseException::class.java)
                .isThrownBy { RetentionApplication(testSettings).run(customArguments(arrayOf("--verbose", "--fake-date=asdf"))) }
    }
}
