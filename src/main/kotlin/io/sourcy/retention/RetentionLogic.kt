package io.sourcy.retention

import org.slf4j.LoggerFactory
import java.io.File

// TODO: verbose logging
class RetentionLogic(private val arguments: Arguments,
                     private val settings: Settings) {
    private val log = LoggerFactory.getLogger(javaClass)

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
