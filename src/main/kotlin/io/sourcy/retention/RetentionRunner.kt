package io.sourcy.retention

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SpringBootApplication
@EnableConfigurationProperties(Settings::class)
class RetentionRunner(val settings: Settings) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        val useTheForce = forceArgument(args)
        val dryRun = dryRunArgument(args)
        val baseDate = fakeDateArgumentOrNow(args)

        //Retention(settings).run(dryRun, useTheForce)
    }

    companion object {
        private val log = LoggerFactory.getLogger(RetentionRunner::class.java)

        fun forceArgument(args: ApplicationArguments) = args.containsOption("force")

        fun dryRunArgument(args: ApplicationArguments) = args.containsOption("dry")

        fun fakeDateArgumentOrNow(args: ApplicationArguments): LocalDate =
                try {
                    fakeDateArgument(args) ?: LocalDate.now()
                } catch (e: Exception) {
                    log.error("Unable to parse fake date. Please provide date in ISO date format (YYYY-MM-DD).")
                    throw e
                }


        private fun fakeDateArgument(args: ApplicationArguments) : LocalDate? =
                if (args.containsOption("fake-date")) {
                    args.getOptionValues("fake-date").first()
                            .let { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) }
                } else {
                    null
                }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(RetentionRunner::class.java, *args)
}
