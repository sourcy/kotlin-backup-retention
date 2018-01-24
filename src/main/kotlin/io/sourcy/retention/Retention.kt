package io.sourcy.retention

import org.slf4j.LoggerFactory

class Retention(val settings: Settings) {

    private val log = LoggerFactory.getLogger(Retention::class.java)

    fun run(dry: Boolean, force: Boolean) {
        log.info("Starting to delete old backups.")
        log.info("Settings: {})", settings)
        log.info("Force: {}", force)
        TODO("do delete here")
        log.info("Finished deleting old backups.")
    }

    fun dryRun() {
        log.info("Previewing.")
    }
}