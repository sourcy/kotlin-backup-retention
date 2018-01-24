package io.sourcy.retention

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.DefaultApplicationArguments
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import java.time.format.DateTimeParseException

@RunWith(SpringRunner::class)
@SpringBootTest
class RetentionRunnerTest {

	@Test
	fun contextLoads() {
	}

	@Test
	fun trueIfForce() {
		val args = DefaultApplicationArguments(arrayOf("--force"))
		assertThat(RetentionRunner.forceArgument(args))
				.isTrue()
	}

	@Test
	fun falseIfNoForce() {
		val args = DefaultApplicationArguments(arrayOf())
		assertThat(RetentionRunner.forceArgument(args))
				.isFalse()
	}

	@Test
	fun trueIfDry() {
		val args = DefaultApplicationArguments(arrayOf("--dry"))
		assertThat(RetentionRunner.dryRunArgument(args))
				.isTrue()
	}

	@Test
	fun falseIfNoDry() {
		val args = DefaultApplicationArguments(arrayOf())
		assertThat(RetentionRunner.dryRunArgument(args))
				.isFalse()
	}

	@Test
	fun nowIfNoFakeDate() {
		val args = DefaultApplicationArguments(arrayOf())
		assertThat(RetentionRunner.fakeDateArgumentOrNow(args))
				.isEqualTo((LocalDate.now()))
	}

	@Test
	fun canParseFakeDate() {
		val args = DefaultApplicationArguments(arrayOf("--fake-date=2017-02-15"))
		assertThat(RetentionRunner.fakeDateArgumentOrNow(args))
				.isEqualTo((LocalDate.of(2017, 2, 15)))
	}

	@Test
	fun failsForFakeDateWithoutValue1() {
		val args = DefaultApplicationArguments(arrayOf("--fake-date"))
		assertThatExceptionOfType(NoSuchElementException::class.java)
				.isThrownBy { RetentionRunner.fakeDateArgumentOrNow(args) }
	}

	@Test
	fun failsForFakeDateWithoutValue2() {
		assertThatExceptionOfType(IllegalArgumentException::class.java)
				.isThrownBy { DefaultApplicationArguments(arrayOf("--fake-date=")) }
	}

	@Test
	fun failsForFakeDateWithInvalidValue1() {
		val args = DefaultApplicationArguments(arrayOf("--fake-date=15.2.2018"))
		assertThatExceptionOfType(DateTimeParseException::class.java)
				.isThrownBy { RetentionRunner.fakeDateArgumentOrNow(args) }
	}

	@Test
	fun failsForFakeDateWithInvalidValue2() {
		val args = DefaultApplicationArguments(arrayOf("--fake-date=2-15-2017"))
		assertThatExceptionOfType(DateTimeParseException::class.java)
				.isThrownBy { RetentionRunner.fakeDateArgumentOrNow(args) }
	}

	@Test
	fun failsForFakeDateWithInvalidValue3() {
		val args = DefaultApplicationArguments(arrayOf("--fake-date=15-2-2017"))
		assertThatExceptionOfType(DateTimeParseException::class.java)
				.isThrownBy { RetentionRunner.fakeDateArgumentOrNow(args) }
	}

	@Test
	fun failsForFakeDateWithInvalidValue4() {
		val args = DefaultApplicationArguments(arrayOf("--fake-date=2017-15-02"))
		assertThatExceptionOfType(DateTimeParseException::class.java)
				.isThrownBy { RetentionRunner.fakeDateArgumentOrNow(args) }
	}
}
