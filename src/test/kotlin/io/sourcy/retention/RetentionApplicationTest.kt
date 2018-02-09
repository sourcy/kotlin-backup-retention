package io.sourcy.retention

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.format.DateTimeParseException

class RetentionApplicationTest : AbstractBaseTest() {
    @Test
    fun `fails a dry run without --force`() {
        Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { main(dryTestRunArguments + "--verbose") }
                .withCauseInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `fails a real run without --force`() {
        Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { main(realTestRunArguments + "--verbose") }
                .withCauseInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `executes a dry run with --force`() {
        main(dryTestRunArguments + arrayOf("--verbose","--force"))
    }

    @Test
    @Disabled("deletes files, breaks other tests")
    fun `executes a real run with --force`() {
        main(realTestRunArguments + arrayOf("--verbose","--force"))
    }


}
