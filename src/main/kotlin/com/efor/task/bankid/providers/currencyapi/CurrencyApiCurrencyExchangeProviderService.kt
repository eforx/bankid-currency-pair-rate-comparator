package com.efor.task.bankid.providers.currencyapi

import com.efor.task.bankid.providers.Currencies
import com.efor.task.bankid.providers.CurrencyExchangeProviderId
import com.efor.task.bankid.providers.CurrencyExchangeProviderService
import com.efor.task.bankid.providers.CurrencyPair
import com.efor.task.bankid.providers.currencyapi.api.CurrencyApi
import com.efor.task.bankid.providers.normalizeCurrencyName
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class CurrencyApiCurrencyExchangeProviderService(
    private val currencyApi: CurrencyApi,
) : CurrencyExchangeProviderService {
    companion object {
        private val logger = LoggerFactory.getLogger(CurrencyApiCurrencyExchangeProviderService::class.java)
    }

    override fun identifier(): CurrencyExchangeProviderId = CurrencyExchangeProviderId.CURRENCY_API

    override fun getCurrencyPairs(): List<CurrencyPair> {
        logger.info("Get currency pairs from Currency API ...")
        val response = currencyApi.fetchCurrencies()
        return response.getNames()
            .flatMap {
                listOf(
                    CurrencyPair(Currencies.CZK, it.key.normalizeCurrencyName()),
                    CurrencyPair(it.key.normalizeCurrencyName(), Currencies.CZK),
                )
            }
    }

    override fun getExchangeRate(
        sourceCurrency: String,
        destCurrency: String,
    ): BigDecimal {
        logger.info("Get exchange rate. sourceCurrency='{}', destCurrency='{}'", sourceCurrency, destCurrency)

        val response = currencyApi.fetchCurrencyRates(sourceCurrency)

        val currencyRates =
            response.rates[sourceCurrency.lowercase()]
                ?: throw IllegalStateException(
                    "Exceptected currency exchange rate list not found. currency='$sourceCurrency'",
                )

        return currencyRates[destCurrency.lowercase()]
            ?: throw IllegalArgumentException("Destination currency not found. destCurrency='$destCurrency'")
    }
}
