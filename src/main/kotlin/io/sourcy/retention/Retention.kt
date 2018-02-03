package io.sourcy.retention

import mu.KLogging

class Retention(private val arguments: Arguments,
                private val settings: Settings) {
    companion object : KLogging()

    fun run() {
        logger.info { "Starting retention." }
        if (arguments.verbose) {
            logger.info { "Settings: $settings" }
            logger.info { "Arguments: $arguments" }
        }

        val retentionFinder = RetentionFinder(arguments, settings)
        val retentionLogic = RetentionLogic(arguments, settings)
        val retentionReport = RetentionReport(arguments)
        val retentionRunner = RetentionRunner(arguments)

        retentionFinder
                .findMatchingFilesIn(arguments.directories)
                .map(retentionLogic::calculateRetentionInfo)
                .also(retentionReport::printInfo)
                .let(retentionRunner::run)
                .also(retentionReport::printResult)

        logger.info { "Finished retention." }
    }
}