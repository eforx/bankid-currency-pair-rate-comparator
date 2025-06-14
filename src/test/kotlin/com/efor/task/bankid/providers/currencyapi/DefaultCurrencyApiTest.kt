package com.efor.task.bankid.providers.currencyapi

import com.efor.task.bankid.providers.currencyapi.api.CurrencyApi
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import org.wiremock.spring.InjectWireMock
import java.math.BigDecimal

@SpringBootTest(classes = [CurrencyApiConfig::class])
@EnableWireMock(
    value = [
        ConfigureWireMock(
            name = "currency-api-service",
            baseUrlProperties = ["external.currency-api.url"],
        ),
    ],
)
class DefaultCurrencyApiTest(
    @Autowired private val currencyApi: CurrencyApi,
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

        val response = currencyApi.fetchCurrencies()

        assertThat(response.getNames())
            .hasSize(340)
            .containsEntry("czk", "Czech Koruna")
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

        val response = currencyApi.fetchCurrencyRates("czk")

        assertThat(response.rates).containsKey("czk")
        assertThat(response.rates["czk"])
            .hasSize(340)
            .containsEntry("eur", BigDecimal("0.040379"))
            .containsEntry("czk", BigDecimal("1.000000"))
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

        val response = currencyApi.fetchCurrencyRates("eur")

        assertThat(response.rates).containsKey("eur")
        assertThat(response.rates["eur"])
            .hasSize(340)
            .containsEntry("eur", BigDecimal("1.000000"))
            .containsEntry("czk", BigDecimal("24.765434"))
    }
}
