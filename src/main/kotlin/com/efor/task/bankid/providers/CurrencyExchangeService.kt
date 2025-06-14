package com.efor.task.bankid.providers

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * Service for performing operations related to currency exchange providers.
 */

interface CurrencyExchangeService {
    /**
     * Retrieves a list of currency pairs that are common between two specified providers.
     *
     * @param providerA The first currency exchange provider.
     * @param providerB The second currency exchange provider.
     * @return A list of common currency pairs supported by both providers.
     * @throws IllegalArgumentException If the specified providers are the same.
     */
    fun getCurrencyPairs(
        providerA: CurrencyExchangeProviderId,
        providerB: CurrencyExchangeProviderId
    ): List<CurrencyPair>

    /**
     * Calculates the difference in exchange rates for a given currency pair between two providers.
     *
     * @param sourceProvider The first provider to retrieve exchange rates from.
     * @param destProvider The second provider to retrieve exchange rates from.
     * @param sourceCurrency The source currency of the exchange rate pair.
     * @param destCurrency The destination currency of the exchange rate pair.
     * @return The difference in exchange rates as a BigDecimal.
     * @throws IllegalArgumentException If the specified providers are the same
     *                                  or if the source and destination currencies are the same.
     */
    fun calculateCurrencyExchangeRateDiff(
        sourceProvider: CurrencyExchangeProviderId,
        destProvider: CurrencyExchangeProviderId,
        sourceCurrency: String,
        destCurrency: String,
    ): BigDecimal
}

@Service
class DefaultCurrencyExchangeService(
    private val currencyExchangeProviderRegistry: CurrencyExchangeProviderRegistry,
) : CurrencyExchangeService {

    companion object {
        private val logger = LoggerFactory.getLogger(DefaultCurrencyExchangeService::class.java)
    }

    override fun getCurrencyPairs(
        providerA: CurrencyExchangeProviderId,
        providerB: CurrencyExchangeProviderId,
    ): List<CurrencyPair> {
        logger.info("Get currency pairs for providers. providerA={}, providerB={}", providerA, providerB)

        if (providerA == providerB) {
            throw IllegalArgumentException("Cannot compare currencies from the same provider. provider=$providerA")
        }

        val providerServiceA = currencyExchangeProviderRegistry.getProviderService(providerA)
        val providerServiceB = currencyExchangeProviderRegistry.getProviderService(providerB)

        val providerCurrencyPairsA = providerServiceA.getCurrencyPairs()
        val providerCurrencyPairsB = providerServiceB.getCurrencyPairs()

        if (logger.isDebugEnabled) {
            logger.debug(
                "Provider currencies. provider={}, count={}, items={}",
                providerA,
                providerCurrencyPairsA.size,
                providerCurrencyPairsA
            )
            logger.debug(
                "Provider currencies. provider={}, count={}, items={}",
                providerB,
                providerCurrencyPairsB.size,
                providerCurrencyPairsB
            )
        }

        val result = providerCurrencyPairsA.filter { it in providerCurrencyPairsB }

        logger.info("Found {} common currency pairs. providerA={}, providerB={}", result.size, providerA, providerB)
        if (logger.isDebugEnabled) {
            logger.debug("Common currency pairs. providerA={}, providerB={}, items={}", providerA, providerB, result)
        }

        return result
    }

    override fun calculateCurrencyExchangeRateDiff(
        sourceProvider: CurrencyExchangeProviderId,
        destProvider: CurrencyExchangeProviderId,
        sourceCurrency: String,
        destCurrency: String
    ): BigDecimal {
        logger.info(
            "Get currency exchange rate difference. " +
                    "sourceProvider={}, destProvider={}, sourceCurrency='{}', destCurrency='{}'",
            sourceProvider, destProvider, sourceCurrency, destCurrency
        )

        if (sourceProvider == destProvider) {
            throw IllegalArgumentException("Cannot compare currencies from the same provider. provider=$sourceProvider")
        }

        if (sourceCurrency == destCurrency) {
            throw IllegalArgumentException("Cannot compare same currencies. currency='$sourceCurrency'")
        }

        val sourceProviderService = currencyExchangeProviderRegistry.getProviderService(sourceProvider)
        val destProviderService = currencyExchangeProviderRegistry.getProviderService(destProvider)

        val availableCurrencyPairs = getCurrencyPairs(sourceProvider, destProvider)
        if (!availableCurrencyPairs.contains(sourceCurrency to destCurrency)) {
            throw IllegalArgumentException(
                "The requested currency pair is not on the list of available currencies. " +
                        "sourceCurrency='$sourceCurrency', destCurrency='${destCurrency}'"
            )
        }

        val sourceProviderExchangeRate = sourceProviderService.getExchangeRate(sourceCurrency, destCurrency)
        val destProviderServiceExchangeRate = destProviderService.getExchangeRate(sourceCurrency, destCurrency)

        logger.info(
            "Currency exchange rate - source provider. " +
                    "sourceProvider={}, sourceCurrency='{}', destCurrency='{}', rate={}",
            sourceProvider, sourceCurrency, destCurrency, sourceProviderExchangeRate
        )
        logger.info(
            "Currency exchange rate - dest provider. " +
                    "destProvider={}, sourceCurrency='{}', destCurrency='{}', rate={}",
            destProvider, sourceCurrency, destCurrency, destProviderServiceExchangeRate
        )

        return sourceProviderExchangeRate.subtract(destProviderServiceExchangeRate)
            .normalizeCurrencyRate()
            .also {
                logger.info(
                    "Calculated currency exchange rate provider's difference. " +
                            "sourceProvider={}, destProvider={}, sourceCurrency='{}', destCurrency='{}', rateDiff={}",
                    sourceProvider, destProvider, sourceCurrency, destCurrency, it
                )
            }
    }
}