package io.sourcy.retention

import arrow.core.Either
import arrow.core.flatMap
import arrow.data.Try
import arrow.syntax.either.right
import mu.KLogging
import java.io.File
import java.time.LocalDate
import kotlin.math.max


class RetentionLogic(private val arguments: Arguments,
                     private val settings: Settings) {
    companion object : KLogging()

    fun calculate(files: Collection<File>): List<Either<Error, Info>> =
            files.groupBy { it.parentFile }
                    .flatMap { calculateDirectory(it.value) }

    fun calculateDirectory(filesInDirectory: Collection<File>): List<Either<Error, Info>> =
            filesInDirectory
                    .map(::calculateFile)
                    .let(::calculateMinKeep)

    fun calculateFile(file: File): Either<Error, FileOnlyInfo> =
            Try { file.let(::toRetentionInfo) }
                    .toEither()
                    .mapLeft { Error(file, it) }

    private fun toRetentionInfo(file: File): FileOnlyInfo {
        val fileDate = extractLocalDate(file)
        return FileOnlyInfo(file, fileDate, isDaily(fileDate), isWeekly(fileDate), isMonthly(fileDate), isExpired(fileDate))
    }

    private fun calculateMinKeep(filesInDirectory: Collection<Either<Error, FileOnlyInfo>>): List<Either<Error, Info>> {
        val files = filesInDirectory.flatMap { it.toOption().toList() }
        val keptFiles = files.filter { !it.isExpired }
        val numToKeep = max(0, settings.files.minKeepPerDirectory - keptFiles.size)

        val expiredFiles = files.filter { it.isExpired }
        val additionalFilesToKeep = expiredFiles.sortedByDescending { it.fileDate }
                .take(numToKeep)

        return filesInDirectory.map { it.flatMap { Info(it, additionalFilesToKeep.contains(it)).right() } }
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

    data class Info(private val fileOnlyInfo: FileOnlyInfo,
                    val isMinKeep: Boolean,
                    val file: File = fileOnlyInfo.file,
                    val fileDate: LocalDate = fileOnlyInfo.fileDate,
                    val isDaily: Boolean = fileOnlyInfo.isDaily,
                    val isWeekly: Boolean = fileOnlyInfo.isWeekly,
                    val isMonthly: Boolean = fileOnlyInfo.isMonthly,
                    val isExpired: Boolean = fileOnlyInfo.isExpired && !isMinKeep)

    data class FileOnlyInfo(val file: File,
                            val fileDate: LocalDate,
                            val isDaily: Boolean,
                            val isWeekly: Boolean,
                            val isMonthly: Boolean,
                            val isExpired: Boolean)

    data class Error(val file: File,
                     val exception: Throwable)
}
