package com.efor.task.bankid.providers.currencyapi

import com.efor.task.bankid.providers.currencyapi.api.DefaultCurrencyApi
import com.fasterxml.jackson.core.JsonFactoryBuilder
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.StreamReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestClient


@Configuration
@Import(
    DefaultCurrencyApi::class,
    CurrencyApiCurrencyExchangeProviderService::class
)
@EnableConfigurationProperties(CurrencyApiProperties::class)
class CurrencyApiConfig {
    @Bean
    fun currencyApiObjectMapper(): ObjectMapper {
        return JsonMapper.builder()
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .build()
            .registerKotlinModule()
    }

    @Bean
    fun currencyApiRestClient(
        currencyApiProperties: CurrencyApiProperties,
        currencyApiObjectMapper: ObjectMapper
    ): RestClient {
        return RestClient.builder()
            .baseUrl(currencyApiProperties.url)
            .messageConverters { converters ->
                val newConverters = converters.map {
                    if (it is MappingJackson2HttpMessageConverter) {
                        MappingJackson2HttpMessageConverter(currencyApiObjectMapper)
                    } else {
                        it
                    }
                }

                converters.clear()
                converters.addAll(newConverters)
            }
            .build()
    }
}
