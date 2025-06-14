package com.efor.task.bankid.controller

import com.efor.task.bankid.app.CurrencyPairRateComparatorAppConfig
import com.github.tomakehurst.wiremock.WireMockServer
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import org.wiremock.spring.InjectWireMock
import test.CnbServerMock
import test.CurrencyApiServerMock

@SpringBootTest(
    classes = [
        CurrencyPairRateComparatorAppConfig::class,
        JacksonAutoConfiguration::class,
        HttpMessageConvertersAutoConfiguration::class,
        WebMvcAutoConfiguration::class
    ]
)
@AutoConfigureMockMvc
@EnableWireMock(
    value = [
        ConfigureWireMock(
            name = "cnb-service",
            baseUrlProperties = ["external.cnb.url"],
        ),
        ConfigureWireMock(
            name = "currency-api-service",
            baseUrlProperties = ["external.currency-api.url"],
        ),
    ],
)
class CurrencyControllerTest(
    @Autowired private val mockMvc: MockMvc
) {
    @InjectWireMock("cnb-service")
    private lateinit var cnbMockService: WireMockServer
    private lateinit var cnbServerMock: CnbServerMock

    @InjectWireMock("currency-api-service")
    private lateinit var currencyApiMockService: WireMockServer
    private lateinit var currencyApiServerMock: CurrencyApiServerMock

    @BeforeEach
    fun init() {
        cnbServerMock = CnbServerMock(cnbMockService)
        currencyApiServerMock = CurrencyApiServerMock(currencyApiMockService)
    }

    @Test
    fun `getAvailableCurrencyPairs - Unknown provider`() {
        mockMvc.perform(get("/api/currency/pairs?currencyExchangeProviderId=awesome_provider"))
            .andDo(print())
            .andExpect(status().isBadRequest())
    }

    @Test
    fun getAvailableCurrencyPairs() {
        cnbServerMock.mockDailyExchangeRate()
        currencyApiServerMock.mockCurrencyList()

        mockMvc.perform(get("/api/currency/pairs?currencyExchangeProviderId=CURRENCY_API"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pairs", hasSize<Any>(62)))
            .andExpect(jsonPath("$.pairs[?(@.source == 'CZK' && @.dest == 'EUR')]").exists())
            .andExpect(jsonPath("$.pairs[?(@.source == 'EUR' && @.dest == 'CZK')]").exists())

        cnbServerMock.verifyDailyExchangeRateCall()
        currencyApiServerMock.verifyCurrencyListCall()
    }

    @Test
    fun `getCurrencyExchangeRateDiff - Unsupported currency pair`() {
        cnbServerMock.mockDailyExchangeRate()
        currencyApiServerMock.mockCurrencyList()

        mockMvc.perform(get("/api/currency/exchange-rate-diff?currencyExchangeProviderId=CURRENCY_API&sourceCurrency=1&destCurrency=2"))
            .andDo(print())
            .andExpect(status().isBadRequest())

        cnbServerMock.verifyDailyExchangeRateCall()
        currencyApiServerMock.verifyCurrencyListCall()
    }

    @Test
    fun `getCurrencyExchangeRateDiff - CZK-EUR`() {
        cnbServerMock.mockDailyExchangeRate()
        currencyApiServerMock.mockCurrencyList()
        currencyApiServerMock.mockCzkExchangeRate()

        mockMvc.perform(get("/api/currency/exchange-rate-diff?currencyExchangeProviderId=CURRENCY_API&sourceCurrency=CZK&destCurrency=EUR"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.exchangeRateDiff").value("-0.000024"))

        cnbServerMock.verifyDailyExchangeRateCall(2)
        currencyApiServerMock.verifyCurrencyListCall()
        currencyApiServerMock.verifyCzkExchangeRateCall()
    }

    @Test
    fun `getCurrencyExchangeRateDiff - EUR-CZK`() {
        cnbServerMock.mockDailyExchangeRate()
        currencyApiServerMock.mockCurrencyList()
        currencyApiServerMock.mockEurExchangeRate()

        mockMvc.perform(get("/api/currency/exchange-rate-diff?currencyExchangeProviderId=CURRENCY_API&sourceCurrency=EUR&destCurrency=CZK"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.exchangeRateDiff").value("0.014566"))

        cnbServerMock.verifyDailyExchangeRateCall(2)
        currencyApiServerMock.verifyCurrencyListCall()
        currencyApiServerMock.verifyEurExchangeRateCall()
    }

}