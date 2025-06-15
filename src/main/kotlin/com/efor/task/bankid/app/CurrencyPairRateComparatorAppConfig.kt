package com.efor.task.bankid.app

import com.efor.task.bankid.common.GlobalExceptionHandler
import com.efor.task.bankid.controller.CurrencyControllerConfig
import com.efor.task.bankid.providers.ProvidersConfig
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.math.BigDecimal

@Configuration
@Import(
    value = [
        GlobalExceptionHandler::class,
        CurrencyControllerConfig::class,
        ProvidersConfig::class,
    ],
)
class CurrencyPairRateComparatorAppConfig {
    @Bean
    fun jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { customizer ->
            customizer.modules(
                SimpleModule()
                    .apply {
                        addSerializer(BigDecimal::class.java, ToStringSerializer.instance)
                    },
            )
            customizer.findModulesViaServiceLoader(true)
            customizer.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }
}
