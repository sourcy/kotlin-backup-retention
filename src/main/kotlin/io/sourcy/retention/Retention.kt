package io.sourcy.retention

import org.slf4j.LoggerFactory

class Retention(val settings: Settings, val arguments: Arguments) {

    private val log = LoggerFactory.getLogger(Retention::class.java)

    fun run() {
        log.info("Starting to delete old backups.")
        log.info("Settings: {})", settings)
        log.info("Arguments: {}", arguments)
        // TODO("do delete here")
        log.info("Finished deleting old backups.")
    }
}