package io.sourcy.retention

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.test.context.TestPropertySource
import java.time.LocalDate
import java.time.DayOfWeek.WEDNESDAY

@TestPropertySource(locations = ["classpath:crazy.properties"])
class SettingsTest : AbstractBaseTest() {
    @Test
    fun loadsSettingsFromProperties() {
        assertThat(settings.dateRegexPattern).isEqualTo(".*_(\\d{8})_.*")
        assertThat(settings.dateRegex().pattern).isEqualTo(".*_(\\d{8})_.*")
        assertThat(settings.dateFormat).isEqualTo("uuuuMMdd")
        assertThat(settings.dateFormatter().format(LocalDate.of(2017, 2, 15)))
                .isEqualTo("20170215")

        assertThat(settings.daily.keep).isEqualTo(1337)
        assertThat(settings.weekly.keep).isEqualTo(1337)
        assertThat(settings.weekly.dayOfWeek).isEqualTo(WEDNESDAY)
        assertThat(settings.monthly.keep).isEqualTo(1337)
        assertThat(settings.monthly.dayOfMonth).isEqualTo(15)

        assertThat(settings.files.maxPercentDelete).isEqualTo(1337)
        assertThat(settings.files.fileNameRegexPatterns)
                .hasSize(2)
                .containsExactlyElementsOf(listOf(".*\\.foo\\.bar",".*\\.far"))
        assertThat(settings.files.fileNameRegexes().map { it.pattern })
                .hasSize(2)
                .containsExactlyElementsOf(listOf(".*\\.foo\\.bar",".*\\.far"))
    }

}