package io.sourcy.retention

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("retention")
data class Settings(
        val daily: DailyRetentionSettings = DailyRetentionSettings(),
        val weekly: WeeklyRetentionSettings = WeeklyRetentionSettings(),
        val monthly: MonthlyRetentionSettings = MonthlyRetentionSettings(),
        var maxPercent: Int? = 10
)

data class DailyRetentionSettings(var keep: Int? = 7)

data class WeeklyRetentionSettings(var keep: Int? = 8)

data class MonthlyRetentionSettings(var keep: Int? = 36)
