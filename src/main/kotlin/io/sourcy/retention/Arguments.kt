package io.sourcy.retention

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Arguments(private val args: ApplicationArguments,
                     val useTheForce : Boolean = useTheForce(args),
                     val dryRun : Boolean = dryRun(args),
                     val baseDate : LocalDate = fakeDateOrNow(args)) {

    companion object {
        private val log = LoggerFactory.getLogger(RetentionRunner::class.java)

        private fun useTheForce(args: ApplicationArguments) = args.containsOption("force")

        private fun dryRun(args: ApplicationArguments) = args.containsOption("dry")

        private fun fakeDateOrNow(args: ApplicationArguments): LocalDate = fakeDate(args) ?: LocalDate.now()

        private fun fakeDate(args: ApplicationArguments): LocalDate? =
                if (args.containsOption("fake-date")) {
                    args.getOptionValues("fake-date")?.first()?.let { parseDate(it) }
                } else {
                    null
                }

        private fun parseDate(it: String?) = try {
            LocalDate.parse(it, DateTimeFormatter.ISO_DATE)
        } catch (e: Exception) {
            log.error("Unable to parse fake date. Please provide date in ISO date format (YYYY-MM-DD).")
            throw e
        }
    }
}