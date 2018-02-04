package io.sourcy.retention

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.springframework.boot.DefaultApplicationArguments
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeParseException

class ArgumentsTest : AbstractBaseTest() {
    @Test
    fun trueIfForce() {
        val args = testArguments(arrayOf("--force", "."))
        assertThat(args.useTheForce)
                .isTrue()
    }

    @Test
    fun falseIfNoForce() {
        val args = testArguments(arrayOf("."))
        assertThat(args.useTheForce)
                .isFalse()
    }

    @Test
    fun trueIfDry() {
        val args = testArguments(arrayOf("--dry", "."))
        assertThat(args.dryRun)
                .isTrue()
    }

    @Test
    fun falseIfNoDry() {
        val args = testArguments(arrayOf("."))
        assertThat(args.dryRun)
                .isFalse()
    }

    @Test
    fun trueIfVerbose() {
        val args = testArguments(arrayOf("--verbose", "."))
        assertThat(args.verbose)
                .isTrue()
    }

    @Test
    fun falseIfNoVerbose() {
        val args = testArguments(arrayOf("."))
        assertThat(args.verbose)
                .isFalse()
    }

    @Test
    fun nowIfNoFakeDate() {
        val args = testArguments(arrayOf("."))
        assertThat(args.theDate)
                .isEqualTo((LocalDate.now()))
    }

    @Test
    fun canParseFakeDate() {
        val args = testArguments(arrayOf("--fake-date=2017-02-15", "."))
        assertThat(args.theDate)
                .isEqualTo((LocalDate.of(2017, 2, 15)))
    }

    @Test
    fun failsForFakeDateWithoutValue1() {
        assertThatExceptionOfType(NoSuchElementException::class.java)
                .isThrownBy { testArguments(arrayOf("--fake-date", ".")) }
    }

    @Test
    fun failsForFakeDateWithoutValue2() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { testArguments(arrayOf("--fake-date=", ".")) }
    }

    @Test
    fun failsForFakeDateWithInvalidValue1() {
        assertThatExceptionOfType(DateTimeParseException::class.java)
                .isThrownBy { testArguments(arrayOf("--fake-date=15.2.2018", ".")) }
    }

    @Test
    fun failsForFakeDateWithInvalidValue2() {
        assertThatExceptionOfType(DateTimeParseException::class.java)
                .isThrownBy { testArguments(arrayOf("--fake-date=2-15-2017", ".")) }
    }

    @Test
    fun failsForFakeDateWithInvalidValue3() {
        assertThatExceptionOfType(DateTimeParseException::class.java)
                .isThrownBy { testArguments(arrayOf("--fake-date=15-2-2017", ".")) }
    }

    @Test
    fun failsForFakeDateWithInvalidValue4() {
        assertThatExceptionOfType(DateTimeParseException::class.java)
                .isThrownBy { testArguments(arrayOf("--fake-date=2017-15-02", ".")) }
    }

    @Test
    fun chosenDirectoryIfSupplied() {
        val dirNames = arrayOf("./src")
        val args = testArguments(dirNames)
        assertThat(args.directories)
                .hasSize(1)
                .containsExactlyElementsOf(expectedDirs(dirNames))
                .allMatch(File::exists)
    }

    @Test
    fun chosenDirectoriesIfSupplied() {
        val dirNames = arrayOf("./src/test", ".", "./src")
        val args = testArguments(dirNames)
        assertThat(args.directories)
                .hasSize(3)
                .containsExactlyElementsOf(expectedDirs(dirNames))
                .allMatch(File::exists)
    }

    @Test
    fun processDirectoryOnlyOnce() {
        val dirNames = arrayOf("./src", "src", "./src/test/../../src")
        val expectedDirNames = arrayOf("./src")
        val args = testArguments(dirNames)
        assertThat(args.directories)
                .hasSize(1)
                .containsExactlyElementsOf(expectedDirs(expectedDirNames))
                .allMatch(File::exists)
    }

    @Test
    fun failsForNoDirectory() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { testArguments(arrayOf()) }
    }

    private fun testArguments(arguments: Array<String>): Arguments =
            Arguments(DefaultApplicationArguments(arguments), testSettings)

    private fun expectedDirs(dirNames: Array<String>): List<File> =
            dirNames.map { File(it).absoluteFile.normalize() }
}
