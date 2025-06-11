package com.efor.task.bankid.cnb

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

interface CnbCurrencyApi {
    /**
     * Fetches the current daily exchange rate from the Czech National Bank (CNB).
     *
     * This method makes an HTTP request to the CNB API endpoint to retrieve the latest
     * daily currency exchange rates. The XML response is parsed and converted into a
     * structured data object.
     *
     * The endpoint used is: /cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.xml
     *
     * @return CnbCurrencyDailyExchangeRateResponse containing the parsed daily exchange rate data
     * @throws org.springframework.web.client.RestClientException if there are issues with the HTTP request
     * @throws CnbCurrencyApiException if the response body is null or cannot be properly processed
     * @see CnbCurrencyDailyExchangeRateResponse
     */
    fun fetchDailyExchangeRate(): CnbCurrencyDailyExchangeRateResponse
}

@Component
class DefaultCnbCurrencyApi(
    private val cnbRestClient: RestClient,
) : CnbCurrencyApi {
    companion object {
        private val logger = LoggerFactory.getLogger(CnbCurrencyApi::class.java)
        private const val DAILY_EXCHANGE_RATE_ENDPOINT =
            "/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.xml"
    }

    override fun fetchDailyExchangeRate(): CnbCurrencyDailyExchangeRateResponse {
        logger.info("Fetching currency daily exchange rates from CNB ...")

        return cnbRestClient
            .get()
            .uri(DAILY_EXCHANGE_RATE_ENDPOINT)
            .retrieve()
            .body(CnbCurrencyDailyExchangeRateResponse::class.java)
            ?.also {
                logger.debug("Currency daily exchange rates have been fetched from CNB. {}", it)
            }
            ?: throw CnbCurrencyApiException("Currency daily exchange rate body is null")
    }
}

class CnbCurrencyApiException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
