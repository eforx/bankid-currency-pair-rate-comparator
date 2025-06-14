package com.efor.task.bankid.app

import com.efor.task.bankid.providers.ProvidersConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    value = [
        ProvidersConfig::class,
    ],
)
class CurrencyPairRateComparatorAppConfig
