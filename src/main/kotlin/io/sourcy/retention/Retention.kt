package io.sourcy.retention

import org.slf4j.LoggerFactory

class Retention(private val settings: Settings, private val arguments: Arguments) {

    private val log = LoggerFactory.getLogger(Retention::class.java)

    fun run() {
        log.info("Starting to delete old backups.")
        log.info("Settings: {})", settings)
        log.info("Arguments: {}", arguments)

        RetentionFiles(arguments.directories.asSequence())
        // TODO("do delete here")
        log.info("Finished deleting old backups.")
    }
}