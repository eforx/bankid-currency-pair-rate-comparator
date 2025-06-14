package com.efor.task.bankid.providers.cnb

import com.efor.task.bankid.providers.Currencies
import com.efor.task.bankid.providers.CurrencyExchangeProviderId
import com.efor.task.bankid.providers.CurrencyExchangeProviderService
import com.efor.task.bankid.providers.CurrencyPair
import com.efor.task.bankid.providers.cnb.api.CnbCurrencyApi
import com.efor.task.bankid.providers.currencyDivide
import com.efor.task.bankid.providers.currencyMultiply
import com.efor.task.bankid.providers.normalizeCurrencyName
import com.efor.task.bankid.providers.normalizeCurrencyRate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class CnbCurrencyExchangeProviderService(
    private val cnbCurrencyApi: CnbCurrencyApi,
) : CurrencyExchangeProviderService {

    companion object {
        private val logger = LoggerFactory.getLogger(CnbCurrencyExchangeProviderService::class.java)
    }

    override fun identifier(): CurrencyExchangeProviderId = CurrencyExchangeProviderId.CNB

    override fun getCurrencyPairs(): List<CurrencyPair> {
        logger.info("Get currency pairs from CNB ...")
        val response = cnbCurrencyApi.fetchDailyExchangeRate()
        return response.table
            .rows
            .flatMap {
                listOf(
                    CurrencyPair(Currencies.CZK, it.code.normalizeCurrencyName()),
                    CurrencyPair(it.code.normalizeCurrencyName(), Currencies.CZK)
                )
            }
    }

    override fun getExchangeRate(sourceCurrency: String, destCurrency: String): BigDecimal {
        logger.info("Get currency exchange rate. sourceCurrency='{}', destCurrency='{}'", sourceCurrency, destCurrency)

        if (Currencies.CZK != sourceCurrency && Currencies.CZK != destCurrency) {
            throw IllegalArgumentException(
                "Unsupported currency. CNB currency provider supports exchange rates " +
                        "for ${Currencies.CZK} currency only. " +
                        "sourceCurrency='${sourceCurrency}', destCurrency='${destCurrency}'",
            )
        }

        val response = cnbCurrencyApi.fetchDailyExchangeRate()
        return if (Currencies.CZK == sourceCurrency) {
            val currencyExchangeInfo = response.table
                .rows
                .find { it.code.normalizeCurrencyName() == destCurrency }
                ?: throw IllegalArgumentException("Destination currency not found. destCurrency='${destCurrency}'")

            1.toBigDecimal()
                .currencyDivide(currencyExchangeInfo.rate)
                .currencyMultiply(currencyExchangeInfo.amount)
                .normalizeCurrencyRate()
        } else {
            val currencyExchangeInfo = response.table
                .rows
                .find { it.code.normalizeCurrencyName() == sourceCurrency }
                ?: throw IllegalArgumentException("Source currency not found. sourceCurrency='${sourceCurrency}'")

            currencyExchangeInfo.rate
                .currencyDivide(currencyExchangeInfo.amount)
                .normalizeCurrencyRate()
        }
    }
}