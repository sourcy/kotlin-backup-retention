package io.sourcy.retention

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.context.TestPropertySource
import java.time.LocalDate
import java.time.DayOfWeek.WEDNESDAY

@TestPropertySource(locations = ["classpath:crazy.properties"])
class SettingsTest : AbstractBaseTest() {
    @Test
    fun loadsSettingsFromProperties() {
        assertThat(testSettings.dateRegexPattern).isEqualTo(".*_(\\d{8})_.*")
        assertThat(testSettings.dateRegex().pattern).isEqualTo(".*_(\\d{8})_.*")
        assertThat(testSettings.dateFormat).isEqualTo("uuuuMMdd")
        assertThat(testSettings.dateFormatter().format(LocalDate.of(2017, 2, 15)))
                .isEqualTo("20170215")

        assertThat(testSettings.daily.keep).isEqualTo(1337)
        assertThat(testSettings.weekly.keep).isEqualTo(1337)
        assertThat(testSettings.weekly.dayOfWeek).isEqualTo(WEDNESDAY)
        assertThat(testSettings.monthly.keep).isEqualTo(1337)
        assertThat(testSettings.monthly.dayOfMonth).isEqualTo(15)

        assertThat(testSettings.files.maxPercentDelete).isEqualTo(1337)
        assertThat(testSettings.files.fileNameRegexPatterns)
                .hasSize(2)
                .containsExactlyElementsOf(listOf(".*\\.foo\\.bar",".*\\.far"))
        assertThat(testSettings.files.fileNameRegexps().map(Regex::pattern))
                .hasSize(2)
                .containsExactlyElementsOf(listOf(".*\\.foo\\.bar",".*\\.far"))
    }

}