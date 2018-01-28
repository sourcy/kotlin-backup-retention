package io.sourcy.retention

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Test
import org.springframework.boot.DefaultApplicationArguments
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeParseException

class ArgumentsTest : AbstractBaseTest() {
    @Test
    fun falseIfNoForce() {
        val applicationArguments = DefaultApplicationArguments(arrayOf())
        val args = Arguments(applicationArguments, settings)
        assertThat(args.useTheForce)
                .isFalse()
    }

    @Test
    fun trueIfDry() {
        val applicationArguments = DefaultApplicationArguments(arrayOf("--dry"))
        val args = Arguments(applicationArguments, settings)
        assertThat(args.dryRun)
                .isTrue()
    }

    @Test
    fun falseIfNoDry() {
        val applicationArguments = DefaultApplicationArguments(arrayOf())
        val args = Arguments(applicationArguments, settings)
        assertThat(args.dryRun)
                .isFalse()
    }

    @Test
    fun nowIfNoFakeDate() {
        val applicationArguments = DefaultApplicationArguments(arrayOf())
        val args = Arguments(applicationArguments, settings)
        assertThat(args.theDate)
                .isEqualTo((LocalDate.now()))
    }

    @Test
    fun canParseFakeDate() {
        val applicationArguments = DefaultApplicationArguments(arrayOf("--fake-date=2017-02-15"))
        val args = Arguments(applicationArguments, settings)
        assertThat(args.theDate)
                .isEqualTo((LocalDate.of(2017, 2, 15)))
    }

    @Test
    fun failsForFakeDateWithoutValue1() {
        val applicationArguments = DefaultApplicationArguments(arrayOf("--fake-date"))
        assertThatExceptionOfType(NoSuchElementException::class.java)
                .isThrownBy { Arguments(applicationArguments, settings) }
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
                .isThrownBy { Arguments(applicationArguments, settings) }
    }

    @Test
    fun failsForFakeDateWithInvalidValue2() {
        val applicationArguments = DefaultApplicationArguments(arrayOf("--fake-date=2-15-2017"))
        assertThatExceptionOfType(DateTimeParseException::class.java)
                .isThrownBy { Arguments(applicationArguments, settings) }

    }

    @Test
    fun failsForFakeDateWithInvalidValue3() {
        val applicationArguments = DefaultApplicationArguments(arrayOf("--fake-date=15-2-2017"))
        assertThatExceptionOfType(DateTimeParseException::class.java)
                .isThrownBy { Arguments(applicationArguments, settings) }
    }

    @Test
    fun failsForFakeDateWithInvalidValue4() {
        val applicationArguments = DefaultApplicationArguments(arrayOf("--fake-date=2017-15-02"))
        assertThatExceptionOfType(DateTimeParseException::class.java)
                .isThrownBy { Arguments(applicationArguments, settings) }
    }

    @Test
    fun currentDirectoryIfNone() {
        val applicationArguments = DefaultApplicationArguments(arrayOf())
        val args = Arguments(applicationArguments, settings)
        assertThat(args.directories)
                .hasSize(1)
                .containsExactlyElementsOf(expectedDirs(arrayOf(".")))
                .allMatch({ dir -> dir.exists() })
    }

    @Test
    fun chosenDirectoryIfSupplied() {
        val dirNames = arrayOf("./src")
        val applicationArguments = DefaultApplicationArguments(dirNames)
        val args = Arguments(applicationArguments, settings)
        assertThat(args.directories)
                .hasSize(1)
                .containsExactlyElementsOf(expectedDirs(dirNames))
                .allMatch({ dir -> dir.exists() })
    }

    @Test
    fun chosenDirectoriesIfSupplied() {
        val dirNames = arrayOf("./src/test", ".", "./src")
        val applicationArguments = DefaultApplicationArguments(dirNames)
        val args = Arguments(applicationArguments, settings)
        assertThat(args.directories)
                .hasSize(3)
                .containsExactlyElementsOf(expectedDirs(dirNames))
                .allMatch({ dir -> dir.exists() })
    }

    @Test
    fun processDirectoryOnlyOnce() {
        val dirNames = arrayOf("./src", "src", "./src/test/../../src")
        val applicationArguments = DefaultApplicationArguments(dirNames)
        val args = Arguments(applicationArguments, settings)
        assertThat(args.directories)
                .hasSize(1)
                .containsExactly(
                        File("./src").absoluteFile.normalize())
                .allMatch({ dir -> dir.exists() })
    }

    private fun expectedDirs(dirNames: Array<String>) =
            dirNames.map { File(it).absoluteFile.normalize() }
}
