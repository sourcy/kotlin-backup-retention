package io.sourcy.retention

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.format.DateTimeParseException

class RetentionApplicationTest : AbstractBaseTest() {
    @Test
    fun executesSuccessfullyDry() {
        main(dryTestRunArguments + arrayOf("--verbose","--force"))
    }

    @Test
    fun executesSuccessfullyReal() {
        main(realTestRunArguments + arrayOf("--verbose","--force"))
    }

    @Test
    fun failsDryWithoutForce() {
        Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { main(dryTestRunArguments + "--verbose") }
                .withCauseInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun failsRealWithoutForce() {
        Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { main(realTestRunArguments + "--verbose") }
                .withCauseInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun failsOnException() {
        Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { main(arrayOf("--verbose", "--fake-date=asdf")) }
                .withCauseInstanceOf(DateTimeParseException::class.java)
    }
}
