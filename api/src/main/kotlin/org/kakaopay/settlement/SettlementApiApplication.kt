package org.kakaopay.settlement

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class SettlementApiApplication

fun main(args: Array<String>) {
    runApplication<SettlementApiApplication>(*args)
}
