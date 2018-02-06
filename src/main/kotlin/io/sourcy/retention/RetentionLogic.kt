package io.sourcy.retention

import arrow.core.Either
import arrow.data.Try
import mu.KLogging
import java.io.File
import java.time.LocalDate


class RetentionLogic(private val arguments: Arguments,
                     private val settings: Settings) {
    companion object : KLogging()

    fun calculateRetentionInfo(file: File): Either<Error, Info> =
            Try { file.let(::toRetentionInfo) }
                    .toEither()
                    .mapLeft { Error(file, it) }

    private fun toRetentionInfo(file: File): Info {
        val fileDate = extractLocalDate(file)
        return Info(file, isDaily(fileDate), isWeekly(fileDate), isMonthly(fileDate), isExpired(fileDate))
    }

    private fun extractLocalDate(file: File): LocalDate =
            settings.parseDate(extractDateString(file.name))

    private fun extractDateString(input: String): String =
            settings.dateRegex().find(input)?.groupValues?.get(1).orEmpty()

    private fun isDaily(fileDate: LocalDate) = true

    private fun isWeekly(fileDate: LocalDate): Boolean =
            fileDate.dayOfWeek == settings.weekly.dayOfWeek

    private fun isMonthly(fileDate: LocalDate): Boolean =
            fileDate.dayOfMonth == settings.monthly.dayOfMonth

    private fun isExpired(fileDate: LocalDate): Boolean =
            !keepDaily(fileDate) &&
                    !keepWeekly(fileDate) &&
                    !keepMonthly(fileDate)

    private fun keepDaily(fileDate: LocalDate): Boolean =
            fileDate.isAfter(arguments.theDate.minusDays(settings.daily.keep))

    private fun keepWeekly(fileDate: LocalDate): Boolean =
            isWeekly(fileDate) && fileDate.isAfter(arguments.theDate.minusWeeks(settings.weekly.keep))

    private fun keepMonthly(fileDate: LocalDate): Boolean =
            isMonthly(fileDate) && fileDate.isAfter(arguments.theDate.minusMonths(settings.monthly.keep))

    data class Info(val file: File,
                    val isDaily: Boolean,
                    val isWeekly: Boolean,
                    val isMonthly: Boolean,
                    val isExpired: Boolean)

    data class Error(val file: File,
                     val exception: Throwable)
}
