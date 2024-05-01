package io.taesu.lockexample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LockExampleApplication

fun main(args: Array<String>) {
    runApplication<LockExampleApplication>(*args)
}
