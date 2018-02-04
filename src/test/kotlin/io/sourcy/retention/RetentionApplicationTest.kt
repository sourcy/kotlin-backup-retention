package io.sourcy.retention

import org.assertj.core.api.Assertions
import org.junit.Test
import java.time.format.DateTimeParseException

class RetentionApplicationTest : AbstractBaseTest() {
    @Test
    fun executesSuccessfullyDry() {
        RetentionApplication(settings).run(dryRunArgumentsAnd(arrayOf("--verbose")))
    }

    @Test
    fun executesSuccessfullyReal() {
        RetentionApplication(settings).run(realRunArgumentsAnd(arrayOf("--verbose")))
    }

    @Test
    fun failsOnException() {
        Assertions.assertThatExceptionOfType(DateTimeParseException::class.java)
                .isThrownBy { RetentionApplication(settings).run(dryRunArgumentsAnd(arrayOf("--fake-date=asdf"))) }
    }
}
