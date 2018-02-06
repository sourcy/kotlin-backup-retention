package io.sourcy.retention

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.DefaultApplicationArguments
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.File

@ExtendWith(SpringExtension::class)
@TestPropertySource(locations = ["classpath:application.properties"])
@EnableConfigurationProperties(Settings::class)
abstract class AbstractBaseTest {
    val testSetDirectory = File("./src/test/resources/testset")
    val realTestRunArguments = arrayOf("--fake-date=2018-01-18", testSetDirectory.absolutePath)
    val dryTestRunArguments = arrayOf(*realTestRunArguments, "--dry")

    @Autowired
    var testSettings = Settings()

    protected fun buildArguments(arguments: Array<String>): Arguments =
            Arguments(DefaultApplicationArguments(arguments), testSettings)
}