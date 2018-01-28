package io.sourcy.retention

import java.io.File

class RetentionDirectories(private val directories: Sequence<File>, val settings: Settings) {
    fun findMatchingFiles() =
            directories.filter { it.exists() }
                    .flatMap { it.walkTopDown() }
                    .filter { it.isFile }
                    .filter { file -> matchesDateRegex(file) }
                    .filter { file -> matchesAnyFileNameRegex(file) }

    private fun matchesDateRegex(file: File) = file.absolutePath.matches(settings.dateRegex())

    private fun matchesAnyFileNameRegex(file: File) =
            settings.files.fileNameRegexes().any { file.absolutePath.matches(it) }
}