package com.efor.task.bankid.providers

import org.springframework.stereotype.Component

/**
 * A registry interface for managing and retrieving [CurrencyExchangeProviderService]
 */
interface CurrencyExchangeProviderRegistry {
    /**
     * Retrieves the service associated with the specified currency exchange provider ID.
     *
     * @param provider The unique identifier for a currency exchange provider.
     * @return The service instance specific to the given provider ID.
     * @throws IllegalArgumentException if no service is found.
     */
    fun getProviderService(provider: CurrencyExchangeProviderId): CurrencyExchangeProviderService
}

@Component
class DefaultCurrencyExchangeProviderRegistry(
    exchangeProviders: List<CurrencyExchangeProviderService>,
) : CurrencyExchangeProviderRegistry {
    val exchangeProviders: Map<CurrencyExchangeProviderId, CurrencyExchangeProviderService> = exchangeProviders
        .associateBy { it.identifier() }

    override fun getProviderService(provider: CurrencyExchangeProviderId): CurrencyExchangeProviderService {
        return exchangeProviders[provider]
            ?: throw IllegalArgumentException("Unsupported currency exchange provider. provider=$provider")
    }
}