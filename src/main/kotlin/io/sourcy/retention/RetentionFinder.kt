package io.sourcy.retention

import org.slf4j.LoggerFactory
import java.io.File

class RetentionFinder(private val arguments: Arguments,
                      private val settings: Settings) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun findMatchingFilesIn(directories: Iterable<File>): Sequence<File> =
            directories.asSequence()
                    .filter(File::exists)
                    .map(File::getAbsoluteFile)
                    .map(File::normalize)
                    .flatMap(File::walkTopDown)
                    .filter(File::isFile)
                    .filter(::matchesDateRegex)
                    .filter(::matchesAnyFileNameRegex)

    private fun matchesDateRegex(file: File): Boolean =
            matchesRegex(file, settings.dateRegex())

    private fun matchesAnyFileNameRegex(file: File): Boolean =
        settings.files.fileNameRegexes().any { matchesRegex(file, it) }

    private fun matchesRegex(file: File, regex: Regex): Boolean =
            file.absolutePath.matches(regex)
                    .also { logRegexCheck(file, regex, it) }

    private fun logRegexCheck(file: File, regex: Regex, it: Boolean) {
        if (arguments.verbose) {
            log.info("Checking File: ${file.absolutePath} for regex: ${regex.pattern}. Result: $it")
        }
    }
}