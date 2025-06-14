package com.efor.task.bankid.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class CurrencyPairRateComparatorApp

fun main(args: Array<String>) {
    runApplication<CurrencyPairRateComparatorApp>(*args)
}
