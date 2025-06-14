package com.efor.task.bankid.providers

import java.math.BigDecimal

typealias CurrencyPair = Pair<String, String>

object Currencies {
    const val CZK = "CZK"
}

/**
 * A service for fetching currency exchange rates and supported currency pairs.
 *
 * Implementations of this interface interact with external APIs to provide
 * currency exchange data.
 */

interface CurrencyExchangeProviderService {
    /**
     * Gets the unique identifier of this currency exchange provider.
     *
     * @return The provider's identifier.
     */
    fun identifier(): CurrencyExchangeProviderId

    /**
     * Gets a list of currency pairs supported by this provider.
     *
     * @return A list of supported currency pairs.
     */
    fun getCurrencyPairs(): List<CurrencyPair>

    /**
     * Gets the exchange rate between two currencies.
     *
     * @param sourceCurrency The currency to convert from.
     * @param destCurrency The currency to convert to.
     * @return The exchange rate.
     * @throws IllegalArgumentException If one of the currencies is not supported.
     * @throws IllegalStateException If the exchange rate cannot be retrieved.
     */
    fun getExchangeRate(
        sourceCurrency: String,
        destCurrency: String,
    ): BigDecimal
}
