package io.sourcy.retention

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ListAssert
import org.junit.jupiter.api.Test
import java.io.File

class RetentionFinderTest : AbstractBaseTest() {

    @Test
    fun `don't find files if no patterns provided`() {
        assertThatMatchingFiles(listOf(), listOf(testSetDirectory))
                .hasSize(0)
    }

    @Test
    fun `don't find files if none matches a regex`() {
        assertThatMatchingFiles(listOf(".*asdf.*"), listOf(testSetDirectory))
                .hasSize(0)
    }

    @Test
    fun `find files matching one expression`() {
        assertThatMatchingFiles(listOf(".*a-domain.*"), listOf(testSetDirectory))
                .hasSize(460)
    }

    @Test
    fun `find files matching one of multiple expressions`() {
        assertThatMatchingFiles(listOf(".*a-domain.*", ".*b-domain.*"), listOf(testSetDirectory))
                .hasSize(474)
    }

    @Test
    fun `find files by file extension expressions`() {
        assertThatMatchingFiles(listOf(".*\\.tar\\.gz", ".*\\.tar\\.bz2"), listOf(testSetDirectory))
                .hasSize(1323)
    }

    @Test
    fun `find files in multiple directories by multiple expressions`() {
        assertThatMatchingFiles(
                listOf(".*\\.tar\\.gz", ".*\\.tar\\.bz2"),
                listOf(testSetDirectory.resolve("a-domain.com"), testSetDirectory.resolve("e-domain.guide"))
        ).hasSize(905)
    }

    private fun assertThatMatchingFiles(patterns: List<String>, directories: List<File>): ListAssert<File> {
        val retentionDirectories = RetentionFinder(buildArguments(defaultTestArguments), testSettingsWith(patterns))
        return assertThat(retentionDirectories.findMatchingFilesIn(directories).toList())
    }

    private fun testSettingsWith(patterns: List<String>) =
            testSettings.copy(files = testSettings.files.copy(fileNameRegexPatterns = patterns))
}