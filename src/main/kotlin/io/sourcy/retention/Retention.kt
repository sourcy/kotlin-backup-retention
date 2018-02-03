package io.sourcy.retention

import org.slf4j.LoggerFactory

class Retention(private val settings: Settings,
                private val arguments: Arguments) {
    private val log = LoggerFactory.getLogger(Retention::class.java)

    fun run() {
        log.info("Starting to delete old backups.")
        log.info("Settings: $settings")
        log.info("Arguments: $arguments")

        val retentionFinder = RetentionFinder(settings)
        val retentionLogic = RetentionLogic(arguments, settings)
        val retentionReport = RetentionReport(arguments)
        val retentionRunner = RetentionRunner(arguments)

        retentionFinder
                .findMatchingFilesIn(arguments.directories)
                .map(retentionLogic::calculateRetentionInfo)
                .also(retentionReport::printInfo)
                .let(retentionRunner::run)
                .also(retentionReport::printResult)
    }
}