package io.sourcy.retention

import java.io.File

class RetentionFinder(private val settings: Settings) {
    fun findMatchingFilesIn(directories: Iterable<File>): Sequence<File> =
            directories.asSequence()
                    .filter(File::exists)
                    .flatMap(File::walkTopDown)
                    .filter(File::isFile)
                    .filter(::matchesDateRegex)
                    .filter(::matchesAnyFileNameRegex)

    private fun matchesDateRegex(file: File): Boolean =
            file.absolutePath.matches(settings.dateRegex())

    private fun matchesAnyFileNameRegex(file: File): Boolean =
            settings.files.fileNameRegexes().any(file.absolutePath::matches)
}