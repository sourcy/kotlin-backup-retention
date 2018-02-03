package io.sourcy.retention

import mu.KLogging
import java.io.File

// TODO: verbose logging
class RetentionRunner(private val arguments: Arguments) {
    companion object: KLogging()

    fun run(retentionInfos: Iterable<RetentionInfo>) : List<RetentionResult> =
            if (arguments.dryRun) {
                emptyList()
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