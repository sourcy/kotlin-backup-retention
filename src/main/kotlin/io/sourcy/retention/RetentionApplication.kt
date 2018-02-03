package io.sourcy.retention

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

@SpringBootApplication
@EnableConfigurationProperties(Settings::class)
class RetentionApplication(private val settings: Settings) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        val arguments = Arguments(args, settings)

        Retention(settings, arguments).run()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(RetentionApplication::class.java, *args)
}
