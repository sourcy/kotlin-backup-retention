package io.sourcy.retention

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import java.io.File
import java.time.LocalDate

data class Arguments(private val args: ApplicationArguments,
                     private val settings: Settings,
                     val useTheForce: Boolean = useTheForce(args),
                     val dryRun: Boolean = dryRun(args),
                     val verbose: Boolean = verbose(args),
                     val theDate: LocalDate = fakeDateOrNow(args, settings),
                     val directories: List<File> = directories(args)) {
    companion object {
        private val log = LoggerFactory.getLogger(Arguments::class.java)
        private const val fakeDateArgumentName = "fake-date"
        private const val forceArgumentName = "force"
        private const val dryArgumentName = "dry"
        private const val verboseArgumentName = "verbose"

        private fun useTheForce(args: ApplicationArguments): Boolean =
                args.containsOption(forceArgumentName)

        private fun dryRun(args: ApplicationArguments): Boolean =
                args.containsOption(dryArgumentName)

        private fun verbose(args: ApplicationArguments): Boolean =
                args.containsOption(verboseArgumentName)

        private fun fakeDateOrNow(args: ApplicationArguments, settings: Settings): LocalDate =
                fakeDate(args, settings) ?: LocalDate.now()

        private fun fakeDate(args: ApplicationArguments, settings: Settings): LocalDate? =
                args.getOptionValues(fakeDateArgumentName)?.first()
                        ?.let { parseDate(it, settings) }

        private fun directories(args: ApplicationArguments): List<File> =
                args.nonOptionArgs.orEmpty()
                        .map(::toNormalizedFile)
                        .distinct()
                        .also (::assertNotEmpty)

        private fun assertNotEmpty(it: List<File>) {
            if (it.isEmpty()) {
                log.error("No directories specified.")
                throw IllegalArgumentException("No directories specified.")
            }
        }

        private fun toNormalizedFile(fileName: String?): File =
                File(fileName).absoluteFile.normalize()

        private fun parseDate(dateString: String?, settings: Settings): LocalDate =
                try {
                    LocalDate.parse(dateString, settings.dateFormatter())
                } catch (e: Exception) {
                    log.error("Unable to parse fake date. Please provide date in format (${settings.dateFormat}).")
                    throw e
                }
    }
}