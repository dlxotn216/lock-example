package io.taesu.lockexample

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import java.time.ZoneId
import java.util.*

@ConfigurationPropertiesScan
@SpringBootApplication
class LockExampleApplication {
    @PostConstruct
    fun onConstruct() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Seoul")))
    }
}

fun main(args: Array<String>) {
    runApplication<LockExampleApplication>(*args)
}
