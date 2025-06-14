package com.efor.task.bankid.providers.cnb

import com.efor.task.bankid.providers.cnb.api.DefaultCnbCurrencyApi
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.client.RestClient
import org.zalando.logbook.Logbook
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor

@Configuration
@Import(
    DefaultCnbCurrencyApi::class,
    CnbCurrencyExchangeProviderService::class,
)
@EnableConfigurationProperties(CnbProperties::class)
class CnbConfig {
    @Bean
    fun cnbRestClient(
        logbook: Logbook?,
        cnbProperties: CnbProperties,
    ): RestClient {
        return RestClient.builder()
            .baseUrl(cnbProperties.url)
            .also { builder ->
                logbook?.let {
                    builder.requestInterceptor(LogbookClientHttpRequestInterceptor(it))
                }
            }
            .build()
    }
}
