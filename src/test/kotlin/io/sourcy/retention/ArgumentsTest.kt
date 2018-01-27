package io.sourcy.retention

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Test
import org.springframework.boot.DefaultApplicationArguments
import java.time.LocalDate
import java.time.format.DateTimeParseException

class ArgumentsTest {

    @Test
    fun falseIfNoForce() {
        val applicationArguments = DefaultApplicationArguments(arrayOf())
        val args = Arguments(applicationArguments)
        assertThat(args.useTheForce)
                .isFalse()
    }

    @Test
    fun trueIfDry() {
        val applicationArguments = DefaultApplicationArguments(arrayOf("--dry"))
        val args = Arguments(applicationArguments)
        assertThat(args.dryRun)
                .isTrue()
    }

    @Test
    fun falseIfNoDry() {
        val applicationArguments = DefaultApplicationArguments(arrayOf())
        val args = Arguments(applicationArguments)
        assertThat(args.dryRun)
                .isFalse()
    }

    @Test
    fun nowIfNoFakeDate() {
        val applicationArguments = DefaultApplicationArguments(arrayOf())
        val args = Arguments(applicationArguments)
        assertThat(args.baseDate)
                .isEqualTo((LocalDate.now()))
    }

    @Test
    fun canParseFakeDate() {
        val applicationArguments = DefaultApplicationArguments(arrayOf("--fake-date=2017-02-15"))
        val args = Arguments(applicationArguments)
        assertThat(args.baseDate)
                .isEqualTo((LocalDate.of(2017, 2, 15)))
    }

    @Test
    fun failsForFakeDateWithoutValue1() {
        val applicationArguments = DefaultApplicationArguments(arrayOf("--fake-date"))
        assertThatExceptionOfType(NoSuchElementException::class.java)
                .isThrownBy { Arguments(applicationArguments) }
    }

    @Test
    fun failsForFakeDateWithoutValue2() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { DefaultApplicationArguments(arrayOf("--fake-date=")) }
    }

    @Test
    fun failsForFakeDateWithInvalidValue1() {
        val applicationArguments = DefaultApplicationArguments(arrayOf("--fake-date=15.2.2018"))
        assertThatExceptionOfType(DateTimeParseException::class.java)
                .isThrownBy { Arguments(applicationArguments) }
    }

    @Test
    fun failsForFakeDateWithInvalidValue2() {
        val applicationArguments = DefaultApplicationArguments(arrayOf("--fake-date=2-15-2017"))
        assertThatExceptionOfType(DateTimeParseException::class.java)
                .isThrownBy { Arguments(applicationArguments) }

    }

    @Test
    fun failsForFakeDateWithInvalidValue3() {
        val applicationArguments = DefaultApplicationArguments(arrayOf("--fake-date=15-2-2017"))
        assertThatExceptionOfType(DateTimeParseException::class.java)
                .isThrownBy { Arguments(applicationArguments) }
    }

    @Test
    fun failsForFakeDateWithInvalidValue4() {
        val applicationArguments = DefaultApplicationArguments(arrayOf("--fake-date=2017-15-02"))
        assertThatExceptionOfType(DateTimeParseException::class.java)
                .isThrownBy { Arguments(applicationArguments) }
    }
}
