package io.sourcy.retention

import org.assertj.core.api.Assertions
import org.junit.Test

class RetentionRunnerTest {

    @Test
    fun executesSuccessfully() {
        main(arrayOf())
    }

    @Test
    fun failsOnException() {
        Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { main(arrayOf("--fake-date=asdf")) }
    }
}
