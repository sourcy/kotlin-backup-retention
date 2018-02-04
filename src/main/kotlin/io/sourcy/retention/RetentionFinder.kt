package io.sourcy.retention

import mu.KLogging
import java.io.File

class RetentionFinder(private val arguments: Arguments,
                      private val settings: Settings) {
    companion object : KLogging()

    fun findMatchingFilesIn(directories: Iterable<File>): List<File> =
            directories.asSequence()
                    .filter(File::exists)
                    .map(File::getAbsoluteFile)
                    .map(File::normalize)
                    .flatMap(File::walkTopDown)
                    .filter(File::isFile)
                    .filter(::matchesDateRegex)
                    .filter(::matchesAnyFileNameRegex)
                    .toList()

    private fun matchesDateRegex(file: File): Boolean =
            matchesRegex(file, settings.dateRegex())

    private fun matchesAnyFileNameRegex(file: File): Boolean =
            settings.files.fileNameRegexps().any { matchesRegex(file, it) }

    private fun matchesRegex(file: File, regex: Regex): Boolean =
            file.absolutePath.matches(regex)
                    .also { logRegexCheck(file, regex, it) }

    private fun logRegexCheck(file: File, regex: Regex, it: Boolean) {
        if (arguments.verbose) {
            logger.info { "Checking File: ${file.absolutePath} for regex: ${regex.pattern}. Result: $it" }
        }
    }
}