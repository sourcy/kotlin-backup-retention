package io.sourcy.retention

import org.assertj.core.api.Assertions
import org.junit.Test
import org.springframework.test.context.TestPropertySource
import java.time.format.DateTimeParseException

@TestPropertySource(locations = ["classpath:test.properties"])
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
