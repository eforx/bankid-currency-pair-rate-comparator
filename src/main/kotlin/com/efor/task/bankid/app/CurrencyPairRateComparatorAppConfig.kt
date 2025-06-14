package com.efor.task.bankid.app

import com.efor.task.bankid.providers.cnb.CnbConfig
import com.efor.task.bankid.providers.currencyrate.CurrencyRateApiConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    value = [
        CnbConfig::class,
        CurrencyRateApiConfig::class,
    ],
)
class CurrencyPairRateComparatorAppConfig
