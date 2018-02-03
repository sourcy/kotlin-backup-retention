package io.sourcy.retention

import org.slf4j.LoggerFactory

// TODO: verbose logging
class RetentionReport(private val arguments: Arguments) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun printInfo(retentionInfo: Sequence<RetentionInfo>) {

    }

    fun printResult(retentionInfo: Sequence<RetentionResult>) {

    }
}