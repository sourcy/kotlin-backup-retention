package io.sourcy.retention

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.DefaultApplicationArguments
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeParseException

class ArgumentsTest : AbstractBaseTest() {
    @Nested
    inner class `test --force argument value` {
        @Test
        fun `true if provided`() {
            val args = testArguments(arrayOf("--force", "."))
            assertThat(args.useTheForce)
                    .isTrue()
        }

        @Test
        fun `false if not provided`() {
            val args = testArguments(arrayOf("."))
            assertThat(args.useTheForce)
                    .isFalse()
        }
    }

    @Nested
    inner class `test --dry argument value` {
        @Test
        fun `true if provided`() {
            val args = testArguments(arrayOf("--dry", "."))
            assertThat(args.dryRun)
                    .isTrue()
        }

        @Test
        fun `false if not provided`() {
            val args = testArguments(arrayOf("."))
            assertThat(args.dryRun)
                    .isFalse()
        }
    }

    @Nested
    inner class `test --verbose argument value` {
        @Test
        fun `true if provided`() {
            val args = testArguments(arrayOf("--verbose", "."))
            assertThat(args.verbose)
                    .isTrue()
        }

        @Test
        fun `false if not provided`() {
            val args = testArguments(arrayOf("."))
            assertThat(args.verbose)
                    .isFalse()
        }
    }

    @Nested
    inner class `test --fake-date argument value` {
        @Test
        fun `is NOW if no fake date provided`() {
            val args = testArguments(arrayOf("."))
            assertThat(args.theDate)
                    .isEqualTo((LocalDate.now()))
        }

        @Test
        fun `can parse valid fakedate`() {
            val args = testArguments(arrayOf("--fake-date=2017-02-15", "."))
            assertThat(args.theDate)
                    .isEqualTo((LocalDate.of(2017, 2, 15)))
        }

        @Test
        fun `fails for fake-date argument without value`() {
            assertThatExceptionOfType(NoSuchElementException::class.java)
                    .isThrownBy { testArguments(arrayOf("--fake-date", ".")) }
        }

        @Test
        fun `fails for fake-date argument with empty value`() {
            assertThatExceptionOfType(IllegalArgumentException::class.java)
                    .isThrownBy { testArguments(arrayOf("--fake-date=", ".")) }
        }

        @Test
        fun `fails for invalid date value (1)`() {
            assertThatExceptionOfType(DateTimeParseException::class.java)
                    .isThrownBy { testArguments(arrayOf("--fake-date=15.2.2018", ".")) }
        }

        @Test
        fun `fails for invalid date value (2)`() {
            assertThatExceptionOfType(DateTimeParseException::class.java)
                    .isThrownBy { testArguments(arrayOf("--fake-date=2-15-2017", ".")) }
        }

        @Test
        fun `fails for invalid date value (3)`() {
            assertThatExceptionOfType(DateTimeParseException::class.java)
                    .isThrownBy { testArguments(arrayOf("--fake-date=15-2-2017", ".")) }
        }

        @Test
        fun `fails for invalid date value (4)`() {
            assertThatExceptionOfType(DateTimeParseException::class.java)
                    .isThrownBy { testArguments(arrayOf("--fake-date=2017-15-02", ".")) }
        }
    }

    @Nested
    inner class `test directories values` {
        @Test
        fun `can parse single directory`() {
            val dirNames = arrayOf("./src")
            val args = testArguments(dirNames)
            assertThat(args.directories)
                    .hasSize(1)
                    .containsExactlyElementsOf(expectedDirs(dirNames))
                    .allMatch(File::exists)
        }

        @Test
        fun `can parse multiple directories`() {
            val dirNames = arrayOf("./src/test", ".", "./src")
            val args = testArguments(dirNames)
            assertThat(args.directories)
                    .hasSize(3)
                    .containsExactlyElementsOf(expectedDirs(dirNames))
                    .allMatch(File::exists)
        }

        @Test
        fun `picks up directory only once if supplied multiple times`() {
            val dirNames = arrayOf("./src", "src", "./src/test/../../src")
            val expectedDirNames = arrayOf("./src")
            val args = testArguments(dirNames)
            assertThat(args.directories)
                    .hasSize(1)
                    .containsExactlyElementsOf(expectedDirs(expectedDirNames))
                    .allMatch(File::exists)
        }

        @Test
        fun `fails if no directories supplied`() {
            assertThatExceptionOfType(IllegalArgumentException::class.java)
                    .isThrownBy { testArguments(arrayOf()) }
        }
    }

    private fun testArguments(arguments: Array<String>): Arguments =
            Arguments(DefaultApplicationArguments(arguments), testSettings)

    private fun expectedDirs(dirNames: Array<String>): List<File> =
            dirNames.map { File(it).absoluteFile.normalize() }
}
