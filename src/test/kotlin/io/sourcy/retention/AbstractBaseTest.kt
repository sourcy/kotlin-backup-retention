package io.sourcy.retention

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.DefaultApplicationArguments
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.io.File

@RunWith(SpringJUnit4ClassRunner::class)
@TestPropertySource(locations = ["classpath:test.properties"])
@EnableConfigurationProperties(Settings::class)
abstract class AbstractBaseTest {
    val testSetDirectory = File("./src/test/resources/testset")

    @Autowired
    var settings = Settings()

    protected fun realRunArgumentsAnd(arguments: Array<String>): Arguments =
            Arguments(DefaultApplicationArguments(arrayOf("--fake-date=2018-01-18", testSetDirectory.absolutePath) + arguments), settings)

    protected fun dryRunArgumentsAnd(arguments: Array<String>): Arguments =
            realRunArgumentsAnd(arguments + arrayOf("--dry"))

}