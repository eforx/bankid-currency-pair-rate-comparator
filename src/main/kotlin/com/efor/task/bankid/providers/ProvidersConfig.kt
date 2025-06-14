package com.efor.task.bankid.providers

import com.efor.task.bankid.providers.cnb.CnbConfig
import com.efor.task.bankid.providers.currencyapi.CurrencyApiConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    value = [
        DefaultCurrencyExchangeProviderRegistry::class,
        DefaultCurrencyExchangeService::class,

        CnbConfig::class,
        CurrencyApiConfig::class,
    ],
)
class ProvidersConfig