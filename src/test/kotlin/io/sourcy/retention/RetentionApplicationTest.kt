package io.sourcy.retention

import org.assertj.core.api.Assertions
import org.junit.Test

class RetentionApplicationTest : AbstractBaseTest() {
    @Test
    fun executesSuccessfully() {
        main(arrayOf("--dry-run", "."))
    }

    @Test
    fun failsOnException() {
        Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { main(arrayOf("--fake-date=asdf")) }
    }
}
