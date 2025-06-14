package com.efor.task.bankid.providers

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * Service for performing operations related to currency exchange providers.
 */
interface CurrencyExchangeService {
    /**
     * Retrieves a list of currency pairs that are common between two specified providers.
     *
     * This method identifies currency pairs that are supported by both providers based on
     * their available data. It ensures that only common pairs are returned, making it useful
     * for comparisons or further processing involving both providers.
     *
     * @param providerA The identifier for the first currency exchange provider.
     * @param providerB The identifier for the second currency exchange provider.
     * @return A list of currency pairs that are supported by both providers.
     * @throws IllegalArgumentException If the specified providers have the same identifier.
     *                                  The providers must be distinct to ensure a meaningful comparison.
     */

    fun getCurrencyPairs(
        providerA: CurrencyExchangeProviderId,
        providerB: CurrencyExchangeProviderId,
    ): List<CurrencyPair>

    /**
     * Calculates the difference in exchange rates for a specific currency pair between two providers.
     *
     * This method fetches the exchange rates of the specified currency pair from both providers
     * and computes the difference. It validates that the providers and the currencies are distinct
     * to ensure the comparison is valid.
     *
     * @param sourceProvider The identifier for the first provider to retrieve exchange rates from.
     * @param destProvider The identifier for the second provider to retrieve exchange rates from.
     * @param sourceCurrency The base currency of the currency pair being compared.
     * @param destCurrency The quote currency of the currency pair being compared.
     * @return The difference in exchange rates between the two providers as a `BigDecimal`.
     * @throws IllegalArgumentException If the specified providers are the same, the source and
     *                                  destination currencies are the same, or the currency pair is not
     *                                  supported by both providers.
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

    @Cacheable("getCurrencyPairs")
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
                providerCurrencyPairsA,
            )
            logger.debug(
                "Provider currencies. provider={}, count={}, items={}",
                providerB,
                providerCurrencyPairsB.size,
                providerCurrencyPairsB,
            )
        }

        val result = providerCurrencyPairsA.filter { it in providerCurrencyPairsB }

        logger.info("Found {} common currency pairs. providerA={}, providerB={}", result.size, providerA, providerB)
        if (logger.isDebugEnabled) {
            logger.debug("Common currency pairs. providerA={}, providerB={}, items={}", providerA, providerB, result)
        }

        return result
    }

    @Cacheable("calculateCurrencyExchangeRateDiff")
    override fun calculateCurrencyExchangeRateDiff(
        sourceProvider: CurrencyExchangeProviderId,
        destProvider: CurrencyExchangeProviderId,
        sourceCurrency: String,
        destCurrency: String,
    ): BigDecimal {
        logger.info(
            "Get currency exchange rate difference. " +
                "sourceProvider={}, destProvider={}, sourceCurrency='{}', destCurrency='{}'",
            sourceProvider,
            destProvider,
            sourceCurrency,
            destCurrency,
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
                    "sourceCurrency='$sourceCurrency', destCurrency='$destCurrency'",
            )
        }

        val sourceProviderExchangeRate = sourceProviderService.getExchangeRate(sourceCurrency, destCurrency)
        val destProviderServiceExchangeRate = destProviderService.getExchangeRate(sourceCurrency, destCurrency)

        logger.info(
            "Currency exchange rate - source provider. " +
                "sourceProvider={}, sourceCurrency='{}', destCurrency='{}', rate={}",
            sourceProvider,
            sourceCurrency,
            destCurrency,
            sourceProviderExchangeRate,
        )
        logger.info(
            "Currency exchange rate - dest provider. " +
                "destProvider={}, sourceCurrency='{}', destCurrency='{}', rate={}",
            destProvider,
            sourceCurrency,
            destCurrency,
            destProviderServiceExchangeRate,
        )

        return sourceProviderExchangeRate.subtract(destProviderServiceExchangeRate)
            .normalizeCurrencyRate()
            .also {
                logger.info(
                    "Calculated currency exchange rate provider's difference. " +
                        "sourceProvider={}, destProvider={}, sourceCurrency='{}', destCurrency='{}', rateDiff={}",
                    sourceProvider,
                    destProvider,
                    sourceCurrency,
                    destCurrency,
                    it,
                )
            }
    }
}
