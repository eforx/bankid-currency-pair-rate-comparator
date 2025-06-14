package com.efor.task.bankid.controller

import com.efor.task.bankid.providers.CurrencyExchangeProviderId
import com.efor.task.bankid.providers.CurrencyExchangeService
import com.efor.task.bankid.providers.normalizeCurrencyName
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/api/currency"],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class CurrencyController(
    private val currencyExchangeService: CurrencyExchangeService
) {
    @GetMapping("/pairs", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAvailableCurrencyPairs(
        @RequestParam currencyExchangeProviderId: CurrencyExchangeProviderId
    ): CurrencyPairsResponse {
        return currencyExchangeService.getCurrencyPairs(
            providerA = CurrencyExchangeProviderId.CNB,
            providerB = currencyExchangeProviderId,
        ).let { pairs ->
            CurrencyPairsResponse(pairs.map {
                CurrencyPair(
                    source = it.first,
                    dest = it.second
                )
            })
        }
    }

    @GetMapping("/exchange-rate-diff")
    fun getCurrencyExchangeRateDiff(
        @RequestParam currencyExchangeProviderId: CurrencyExchangeProviderId,
        @RequestParam sourceCurrency: String,
        @RequestParam destCurrency: String,
    ): CurrencyExchangeRateDiffResponse {
        return currencyExchangeService.calculateCurrencyExchangeRateDiff(
            sourceProvider = CurrencyExchangeProviderId.CNB,
            destProvider = currencyExchangeProviderId,
            sourceCurrency = sourceCurrency.normalizeCurrencyName(),
            destCurrency = destCurrency.normalizeCurrencyName(),
        ).let { CurrencyExchangeRateDiffResponse(it) }
    }
}