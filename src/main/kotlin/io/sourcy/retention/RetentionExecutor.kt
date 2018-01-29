package io.sourcy.retention

import java.io.File

class RetentionExecutor(private val arguments: Arguments) {
    fun execute(retentionInfos: Sequence<RetentionInfo>) =
            if (arguments.dryRun) {
                emptySequence<RetentionInfo>()
            } else {
                retentionInfos.map { execute(it) }
            }

    private fun execute(retentionInfo: RetentionInfo) =
            when (retentionInfo) {
                is RetentionInfo.Info -> executeRetention(retentionInfo)
                is RetentionInfo.Error -> RetentionResult.Error(retentionInfo.file, retentionInfo.exception)
            }

    private fun executeRetention(retentionInfo: RetentionInfo.Info) =
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