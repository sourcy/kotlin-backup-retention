package io.sourcy.retention

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ListAssert
import org.junit.jupiter.api.Test
import java.io.File

class RetentionFinderTest : AbstractBaseTest() {

    @Test
    fun noMatchingFilesInNoPatterns() {
        assertThatMatchingFiles(listOf(), listOf(testSetDirectory))
                .hasSize(0)
    }

    @Test
    fun noMatchingFiles() {
        assertThatMatchingFiles(listOf(".*asdf.*"), listOf(testSetDirectory))
                .hasSize(0)
    }

    @Test
    fun matchingFilesOneRegex() {
        assertThatMatchingFiles(listOf(".*a-domain.*"), listOf(testSetDirectory))
                .hasSize(460)
    }

    @Test
    fun matchingFilesMultiRegex() {
        assertThatMatchingFiles(listOf(".*a-domain.*", ".*b-domain.*"), listOf(testSetDirectory))
                .hasSize(474)
    }

    @Test
    fun matchingFilesByExtension() {
        assertThatMatchingFiles(listOf(".*\\.tar\\.gz", ".*\\.tar\\.bz2"), listOf(testSetDirectory))
                .hasSize(1323)
    }

    @Test
    fun matchingFilesMultipleDirectories() {
        assertThatMatchingFiles(
                listOf(".*\\.tar\\.gz", ".*\\.tar\\.bz2"),
                listOf(testSetDirectory.resolve("a-domain.com"), testSetDirectory.resolve("e-domain.guide"))
        ).hasSize(905)
    }

    private fun assertThatMatchingFiles(patterns: List<String>, directories: List<File>): ListAssert<File> {
        val retentionDirectories = RetentionFinder(buildArguments(dryTestRunArguments), testSettingsWith(patterns))
        return assertThat(retentionDirectories.findMatchingFilesIn(directories).toList())
    }

    private fun testSettingsWith(patterns: List<String>) =
            testSettings.copy(files = testSettings.files.copy(fileNameRegexPatterns = patterns))
}