package com.efor.task.bankid.providers.currencyapi.api

import com.efor.task.bankid.providers.normalizeCurrencyRate
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

/**
 * Generated
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class CurrencyRatesResponse(
    @JsonProperty("date")
    val date: String,
    // This holds all currency blocks, mapping from currency (eur, usd, gbp, etc) to rates
    val rates: MutableMap<String, Map<String, BigDecimal>> = mutableMapOf(),
) {
    // TODO: Find some other better/cleaner way to deserialize a nested json map containing decimals?
    @JsonAnySetter
    fun addRate(
        currency: String,
        ratesMap: Map<String, BigDecimal>,
    ) {
        rates[currency] = ratesMap.mapValues { it.value.normalizeCurrencyRate() }
    }
}
