package io.sourcy.retention

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.DayOfWeek
import java.time.DayOfWeek.SUNDAY
import java.time.format.DateTimeFormatter

@ConfigurationProperties("retention")
data class Settings(
        var dateRegexPattern: String = ".*_\\d{4}-\\d{2}-\\d{2}_.*",
        var dateFormat: String = "yyyy-MM-dd",
        val daily: DailyRetentionSettings = DailyRetentionSettings(),
        val weekly: WeeklyRetentionSettings = WeeklyRetentionSettings(),
        val monthly: MonthlyRetentionSettings = MonthlyRetentionSettings(),
        val files: FileSettings = FileSettings()) {
    fun dateRegex() =
            Regex(dateRegexPattern)
    fun dateFormatter() =
            DateTimeFormatter.ofPattern(dateFormat)!!
}

data class DailyRetentionSettings(var keep: Int = 7)

data class WeeklyRetentionSettings(var keep: Int = 8,
                                   var dayOfWeek: DayOfWeek = SUNDAY)

data class MonthlyRetentionSettings(var keep: Int = 36,
                                    var dayOfMonth: Int = 1)

data class FileSettings(var maxPercentDelete: Int = 10,
                        var fileNameRegexPatterns: List<String> = emptyList()) {
    fun fileNameRegexes() =
            fileNameRegexPatterns.map { Regex(it)}
}