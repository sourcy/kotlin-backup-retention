package io.sourcy.retention

import io.github.glytching.junit.extension.folder.TemporaryFolder
import io.github.glytching.junit.extension.folder.TemporaryFolderExtension
import org.apache.commons.io.FileUtils
import org.assertj.core.api.Assertions
import org.assertj.core.api.ListAssert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File

class RetentionApplicationTest : AbstractBaseTest() {
    @Test
    fun `fails a dry run without --force`() {
        Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy {
                    main(arrayOf("--dry", "--verbose", testSetFakeDate, testSetDirectory.absolutePath))
                }
                .withCauseInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `fails a real run without --force`() {
        Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy {
                    main(arrayOf("--verbose", testSetFakeDate, testSetDirectory.absolutePath))
                }
                .withCauseInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `executes a dry run with --force but doesn't delete files`() {
        assertThatMatchingFiles(listOf(testSetDirectory))
                .hasSize(1324)

        main(arrayOf("--dry", "--verbose","--force", testSetFakeDate, testSetDirectory.absolutePath))

        assertThatMatchingFiles(listOf(testSetDirectory))
                .hasSize(1324)
    }

    @Test
    @ExtendWith(TemporaryFolderExtension::class)
    fun `executes a real run with --force and deletes expires files`(tempFolder: TemporaryFolder) {
        val tempTestSetDirectory = tempFolder.createDirectory("temptestset")
        FileUtils.copyDirectory(testSetDirectory, tempTestSetDirectory)

        assertThatMatchingFiles(listOf(tempTestSetDirectory))
                .hasSize(1324)

        main(arrayOf("--force", testSetFakeDate, tempTestSetDirectory.absolutePath))

        assertThatMatchingFiles(listOf(tempTestSetDirectory))
                .hasSize(126)
    }

    private fun assertThatMatchingFiles(directories: List<File>): ListAssert<File> {
        val retentionDirectories = RetentionFinder(buildArguments(defaultTestArguments), testSettings)
        return Assertions.assertThat(retentionDirectories.findMatchingFilesIn(directories).toList())
    }
}
