package io.sourcy.retention

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.DefaultApplicationArguments
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.io.File

@RunWith(SpringJUnit4ClassRunner::class)
@EnableConfigurationProperties(Settings::class)
abstract class AbstractBaseTest {
    val testSetDirectory = File("./src/test/resources/testset")
    @Autowired
    var settings: Settings = Settings()

    protected fun testArguments(arguments: Array<String>): Arguments {
        return Arguments(DefaultApplicationArguments(arguments), settings)
    }
}