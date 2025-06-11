package com.efor.task.bankid.providers.currencyrate

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import org.wiremock.spring.InjectWireMock

@SpringBootTest(classes = [CurrencyRateApiConfig::class])
@EnableWireMock(
    value = [
        ConfigureWireMock(
            name = "currency-api-service",
            baseUrlProperties = ["external.currency-api.url"],
        ),
    ],
)
class DefaultCurrencyRateApiTest(
    @Autowired private val currencyRateApi: CurrencyRateApi,
) {
    @InjectWireMock("currency-api-service")
    private lateinit var currencyRateApiMockService: WireMockServer

    @Test
    fun fetchCurrencies() {
        currencyRateApiMockService.stubFor(
            get("/npm/@fawazahmed0/currency-api@latest/v1/currencies.json")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json", "charset=utf-8")
                        .withBodyFile("currency_api/currencies.json"),
                ),
        )

        currencyRateApi.fetchCurrencies()
    }

    @Test
    fun fetchCurrencyRates_Czk() {
        currencyRateApiMockService.stubFor(
            get("/npm/@fawazahmed0/currency-api@latest/v1/currencies/czk.json")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json", "charset=utf-8")
                        .withBodyFile("currency_api/currency-czk.json"),
                ),
        )

        currencyRateApi.fetchCurrencyRates("czk")
    }

    @Test
    fun fetchCurrencyRates_Eur() {
        currencyRateApiMockService.stubFor(
            get("/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json", "charset=utf-8")
                        .withBodyFile("currency_api/currency-eur.json"),
                ),
        )

        currencyRateApi.fetchCurrencyRates("eur")
    }
}
