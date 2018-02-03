package io.sourcy.retention

import mu.KLogging
import java.io.File

// TODO: verbose logging
class RetentionLogic(private val arguments: Arguments,
                     private val settings: Settings) {
    companion object : KLogging()

    fun calculateRetentionInfo(file: File) : RetentionInfo =
            try {
                RetentionInfo.Info(file, false, false, false, false)
            } catch (e: Exception) {
                RetentionInfo.Error(file, e)
            }
}

sealed class RetentionInfo {
    class Info(val file: File,
               val isDaily: Boolean,
               val isWeekly: Boolean,
               val isMonthly: Boolean,
               val isExpired: Boolean) : RetentionInfo()

    class Error(val file: File,
                val exception: Exception) : RetentionInfo()
}
