package com.efor.task.bankid.providers.currencyapi.api

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

/**
 * Provides an API to fetch currencies and their exchange rates from an external Currency API.
 */
interface CurrencyApi {
    /**
     * Fetches a list of all available currencies from the external Currency API.
     *
     * @return a {@link CurrenciesResponse} containing the list of available currencies.
     * @throws CurrencyRateApiException if the response body is null or an error occurs during the API call.
     */
    fun fetchCurrencies(): CurrenciesResponse

    /**
     * Fetches exchange rates for a specific currency from the external Currency API.
     *
     * @param currency the currency for which exchange rates are to be fetched. This should be
     *                 provided in lowercase format.
     * @return a {@link CurrencyRatesResponse} containing the exchange rates for the specified currency.
     * @throws CurrencyRateApiException if the response body is null or an error occurs during the API call.
     */
    fun fetchCurrencyRates(currency: String): CurrencyRatesResponse
}

@Component
class DefaultCurrencyApi(
    private val currencyApiRestClient: RestClient,
) : CurrencyApi {
    companion object {
        private val logger = LoggerFactory.getLogger(DefaultCurrencyApi::class.java)
        private const val CURRENCY_LIST_ENDPOINT =
            "/npm/@fawazahmed0/currency-api@latest/v1/currencies.json"
        private const val CURRENCY_RATES_ENDPOINT =
            "npm/@fawazahmed0/currency-api@latest/v1/currencies/{currency}.json"
    }

    override fun fetchCurrencies(): CurrenciesResponse {
        logger.info("Fetching currency list from Currency API ...")

        return currencyApiRestClient
            .get()
            .uri(CURRENCY_LIST_ENDPOINT)
            .retrieve()
            .body(CurrenciesResponse::class.java)
            ?.also {
                if (logger.isDebugEnabled) {
                    logger.debug("Currency list has been fetched from Currency API. {}", it)
                }
            }
            ?: throw CurrencyRateApiException("Currency list body is null")
    }

    override fun fetchCurrencyRates(currency: String): CurrencyRatesResponse {
        logger.info("Fetching currency rates from Currency API ... currency='{}'", currency)

        val uriVariables = mapOf("currency" to currency.lowercase())

        return currencyApiRestClient
            .get()
            .uri(CURRENCY_RATES_ENDPOINT, uriVariables)
            .retrieve()
            .body(CurrencyRatesResponse::class.java)
            ?.also {
                if (logger.isDebugEnabled) {
                    logger.debug(
                        "Currency rates have been fetched from Currency API. currency='{}', rates={}",
                        currency,
                        it.rates,
                    )
                }
            }
            ?: throw CurrencyRateApiException("Currency list body is null")
    }
}

class CurrencyRateApiException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
