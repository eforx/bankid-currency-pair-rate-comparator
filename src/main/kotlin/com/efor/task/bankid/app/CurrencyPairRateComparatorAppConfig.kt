package com.efor.task.bankid.app

import com.efor.task.bankid.cnb.CnbConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(value = [CnbConfig::class])
class CurrencyPairRateComparatorAppConfig
