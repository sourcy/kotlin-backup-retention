package io.sourcy.kotlinbackupretention

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class KotlinBackupRetentionApplication

fun main(args: Array<String>) {
    SpringApplication.run(KotlinBackupRetentionApplication::class.java, *args)
}
