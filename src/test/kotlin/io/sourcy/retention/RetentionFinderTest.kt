package io.sourcy.retention

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class RetentionFinderTest : AbstractBaseTest() {

    @Test
    fun noMatchingFiles() {
        val regexTestSettings = settingsWith(listOf(".*asdf.*"))
        val retentionDirectories = RetentionFinder(regexTestSettings)
        assertThat(retentionDirectories.findMatchingFilesIn(sequenceOf(testSetDirectory)).toList())
                .hasSize(0)
    }

    @Test
    fun matchingFilesOneRegex() {
        val regexTestSettings = settingsWith(listOf(".*a-domain.*"))
        val retentionDirectories = RetentionFinder(regexTestSettings)
        assertThat(retentionDirectories.findMatchingFilesIn(sequenceOf(testSetDirectory)).toList())
                .hasSize(460)
    }

    @Test
    fun matchingFilesMultiRegex() {
        val regexTestSettings = settingsWith(listOf(".*a-domain.*", ".*b-domain.*"))
        val retentionDirectories = RetentionFinder(regexTestSettings)
        assertThat(retentionDirectories.findMatchingFilesIn(sequenceOf(testSetDirectory)).toList())
                .hasSize(474)
    }

    @Test
    fun matchingFilesByExtension() {
        val regexTestSettings = settingsWith(listOf(".*\\.tar\\.gz", ".*\\.tar\\.bz2"))
        val retentionDirectories = RetentionFinder(regexTestSettings)
        assertThat(retentionDirectories.findMatchingFilesIn(sequenceOf(testSetDirectory)).toList())
                .hasSize(1321)
    }

    @Test
    fun matchingFilesMultipleDirectories() {
        val regexTestSettings = settingsWith(listOf(".*\\.tar\\.gz", ".*\\.tar\\.bz2"))
        val retentionDirectories = RetentionFinder(regexTestSettings)
        assertThat(retentionDirectories.findMatchingFilesIn(sequenceOf(testSetDirectory.resolve("a-domain.com"), testSetDirectory.resolve("e-domain.guide"))).toList())
                .hasSize(905)
    }


    private fun settingsWith(patterns: List<String>) =
            settings.copy(files = settings.files.copy(fileNameRegexPatterns = patterns))
}