package io.sourcy.retention

import org.slf4j.LoggerFactory

class Retention(private val settings: Settings,
                private val arguments: Arguments) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun run() {
        log.info("Starting to delete old backups.")
        if (arguments.verbose) {
            log.info("Settings: $settings")
            log.info("Arguments: $arguments")
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

        log.info("Deleting old backups finished.")
    }
}