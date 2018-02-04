package io.sourcy.retention

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(Settings::class)
class RetentionApplication(private val settings: Settings) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        run(Arguments(args, settings))
    }

    fun run(arguments: Arguments) {
        Retention(arguments, settings).run()
    }
}

fun main(args: Array<String>) {
    runApplication<RetentionApplication>(*args)
}
