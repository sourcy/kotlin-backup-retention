package io.sourcy.retention

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import java.io.File
import java.time.LocalDate

data class Arguments(private val args: ApplicationArguments,
                     private val settings: Settings,
                     val useTheForce: Boolean = useTheForce(args),
                     val dryRun: Boolean = dryRun(args),
                     val theDate: LocalDate = fakeDateOrNow(args, settings),
                     val directories: List<File> = directories(args)) {
    companion object {
        private val log = LoggerFactory.getLogger(RetentionRunner::class.java)

        private fun useTheForce(args: ApplicationArguments) = args.containsOption("force")

        private fun dryRun(args: ApplicationArguments) = args.containsOption("dry")

        private fun fakeDateOrNow(args: ApplicationArguments, settings: Settings): LocalDate =
                fakeDate(args, settings) ?: LocalDate.now()

        private fun fakeDate(args: ApplicationArguments, settings: Settings): LocalDate? =
                if (args.containsOption("fake-date")) {
                    args.getOptionValues("fake-date")?.first()?.let { parseDate(it, settings) }
                } else {
                    null
                }

        private fun parseDate(it: String?, settings: Settings) = try {
            LocalDate.parse(it, settings.dateFormatter())
        } catch (e: Exception) {
            log.error("Unable to parse fake date. Please provide date in format (${settings.dateFormat}).")
            throw e
        }

        private fun directories(args: ApplicationArguments) =
                if (args.nonOptionArgs == null || args.nonOptionArgs.size == 0) {
                    listOf(File(".").absoluteFile.normalize())
                } else {
                    args.nonOptionArgs.map { File(it).absoluteFile.normalize() }.distinct()
                }
    }
}