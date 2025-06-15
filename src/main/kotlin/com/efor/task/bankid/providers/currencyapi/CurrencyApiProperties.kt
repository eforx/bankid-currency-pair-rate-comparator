package com.efor.task.bankid.providers.currencyapi

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration of Currency API currency exchange rate provider
 */
@ConfigurationProperties("external.currency-api")
class CurrencyApiProperties(
    /**
     * Currency API service base HTTP url
     */
    val url: String,
)
