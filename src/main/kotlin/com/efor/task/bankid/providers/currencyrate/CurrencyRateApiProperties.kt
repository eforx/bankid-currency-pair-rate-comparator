package com.efor.task.bankid.providers.currencyrate

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("external.currency-api")
class CurrencyRateApiProperties(
    /**
     * Currency API server base HTTP url
     */
    val url: String,
)
