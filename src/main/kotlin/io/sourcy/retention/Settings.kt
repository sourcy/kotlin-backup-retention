package io.sourcy.retention

import mu.KLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.DayOfWeek
import java.time.DayOfWeek.SUNDAY
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ConfigurationProperties("retention")
data class Settings(
        var dateRegexPattern: String = ".*_(\\d{4}-\\d{2}-\\d{2})_.*",
        var dateFormat: String = "yyyy-MM-dd",
        val daily: DailyRetentionSettings = DailyRetentionSettings(),
        val weekly: WeeklyRetentionSettings = WeeklyRetentionSettings(),
        val monthly: MonthlyRetentionSettings = MonthlyRetentionSettings(),
        val files: FileSettings = FileSettings()) {
    companion object : KLogging()

    fun dateRegex(): Regex =
            Regex(dateRegexPattern)
    fun dateFormatter(): DateTimeFormatter =
            DateTimeFormatter.ofPattern(dateFormat)!!
    fun parseDate(dateString: String): LocalDate =
            try {
                LocalDate.parse(dateString, dateFormatter())
            } catch (e: Exception) {
                logger.error { "Unable to parse fake date. Please provide date in format ($dateFormat)." }
                throw e
            }
}

data class DailyRetentionSettings(var keep: Long = 7)

data class WeeklyRetentionSettings(var keep: Long = 8,
                                   var dayOfWeek: DayOfWeek = SUNDAY)

data class MonthlyRetentionSettings(var keep: Long = 36,
                                    var dayOfMonth: Int = 1)

data class FileSettings(var maxPercentDelete: Int = 10,
                        var fileNameRegexPatterns: List<String> = emptyList()) {
    fun fileNameRegexps(): List<Regex> =
            fileNameRegexPatterns.map(::Regex)
}