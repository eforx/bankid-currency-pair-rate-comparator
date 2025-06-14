package com.efor.task.bankid.providers.currencyapi

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration
 */
@ConfigurationProperties("external.currency-api")
class CurrencyApiProperties(
    /**
     * Currency API server base HTTP url
     */
    val url: String,
)
