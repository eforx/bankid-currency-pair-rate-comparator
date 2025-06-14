package com.efor.task.bankid.controller

import com.efor.task.bankid.providers.CurrencyExchangeProviderId

/**
 * Enum representing the available currency exchange providers.
 */
enum class PublicCurrencyExchangeProviderId {
    CURRENCY_API,
}

fun PublicCurrencyExchangeProviderId.toInternal() = CurrencyExchangeProviderId.valueOf(this.name)
