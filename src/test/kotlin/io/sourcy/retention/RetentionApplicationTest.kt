package io.sourcy.retention

import org.assertj.core.api.Assertions
import org.junit.Test
import java.time.format.DateTimeParseException

class RetentionApplicationTest : AbstractBaseTest() {
    @Test
    fun executesSuccessfullyDry() {
        RetentionApplication(settings).run(dryTestRunArgumentsAnd(arrayOf("--verbose")))
    }

    @Test
    fun executesSuccessfullyReal() {
        RetentionApplication(settings).run(testRunArgumentsAnd(arrayOf("--verbose")))
    }

    @Test
    fun failsOnException() {
        Assertions.assertThatExceptionOfType(DateTimeParseException::class.java)
                .isThrownBy { RetentionApplication(settings).run(customArguments(arrayOf("--verbose", "--fake-date=asdf"))) }
    }
}
