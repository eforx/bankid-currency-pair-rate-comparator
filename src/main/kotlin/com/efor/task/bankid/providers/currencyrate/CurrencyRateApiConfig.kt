package com.efor.task.bankid.providers.currencyrate

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.client.RestClient

@Configuration
@Import(
    DefaultCurrencyRateApi::class,
)
@EnableConfigurationProperties(CurrencyRateApiProperties::class)
class CurrencyRateApiConfig {
    @Bean
    fun currencyApiRestClient(currencyRateApiProperties: CurrencyRateApiProperties): RestClient {
        return RestClient.builder()
            .baseUrl(currencyRateApiProperties.url)
            .build()
    }
}
