package io.sourcy.retention

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ListAssert
import org.junit.Test
import java.io.File

class RetentionFinderTest : AbstractBaseTest() {

    @Test
    fun noMatchingFiles() {
        assertMatchingFilesIn(listOf(".*asdf.*"), listOf(testSetDirectory))
                .hasSize(0)
    }

    @Test
    fun matchingFilesOneRegex() {
        assertMatchingFilesIn(listOf(".*a-domain.*"), listOf(testSetDirectory))
                .hasSize(460)
    }

    @Test
    fun matchingFilesMultiRegex() {
        assertMatchingFilesIn(listOf(".*a-domain.*", ".*b-domain.*"), listOf(testSetDirectory))
                .hasSize(474)
    }

    @Test
    fun matchingFilesByExtension() {
        assertMatchingFilesIn(listOf(".*\\.tar\\.gz", ".*\\.tar\\.bz2"), listOf(testSetDirectory))
                .hasSize(1321)
    }

    @Test
    fun matchingFilesMultipleDirectories() {
        assertMatchingFilesIn(
                listOf(".*\\.tar\\.gz", ".*\\.tar\\.bz2"),
                listOf(testSetDirectory.resolve("a-domain.com"), testSetDirectory.resolve("e-domain.guide"))
        ).hasSize(905)
    }

    private fun assertMatchingFilesIn(patterns: List<String>, directories: List<File>): ListAssert<File> {
        val regexTestSettings = settingsWith(patterns)
        val retentionDirectories = RetentionFinder(regexTestSettings)
        return assertThat(retentionDirectories.findMatchingFilesIn(directories).toList())
    }

    private fun settingsWith(patterns: List<String>) =
            settings.copy(files = settings.files.copy(fileNameRegexPatterns = patterns))
}