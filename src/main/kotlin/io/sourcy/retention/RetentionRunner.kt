package io.sourcy.retention

import org.slf4j.LoggerFactory
import java.io.File

// TODO: verbose logging
class RetentionRunner(private val arguments: Arguments) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun run(retentionInfos: Sequence<RetentionInfo>) : Sequence<RetentionResult> =
            if (arguments.dryRun) {
                emptySequence()
            } else {
                // TODO: check for maxDelete & force argument
                retentionInfos.map(::runSingle)
            }

    private fun runSingle(retentionInfo: RetentionInfo): RetentionResult =
            when (retentionInfo) {
                is RetentionInfo.Info ->
                    executeRetention(retentionInfo)
                is RetentionInfo.Error ->
                    RetentionResult.Error(retentionInfo.file, retentionInfo.exception)
            }

    private fun executeRetention(retentionInfo: RetentionInfo.Info): RetentionResult =
            try {
                RetentionResult.Result(retentionInfo.file, false)
            } catch (e: Exception) {
                RetentionResult.Error(retentionInfo.file, e)
            }
}

sealed class RetentionResult {
    class Result(val file: File,
                 val wasDeleted: Boolean) : RetentionResult()

    class Error(val file: File,
                val exception: Exception) : RetentionResult()
}