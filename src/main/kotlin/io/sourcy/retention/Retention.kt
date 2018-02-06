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
        val retentionRunner = RetentionRunner(arguments, settings)

        retentionFinder
                .findMatchingFilesIn(arguments.directories)
                .map(retentionLogic::calculateRetentionInfo)
                .let(retentionRunner::run)

        logger.info { "Finished retention." }
    }
}