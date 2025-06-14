package com.efor.task.bankid.providers

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import org.wiremock.spring.InjectWireMock

@SpringBootTest(classes = [ProvidersConfig::class])
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
class DefaultCurrencyExchangeServiceTest(
    @Autowired private val currencyExchangeService: CurrencyExchangeService,
) {
    @InjectWireMock("cnb-service")
    private lateinit var cnbMockService: WireMockServer

    @InjectWireMock("currency-api-service")
    private lateinit var currencyRateApiMockService: WireMockServer

    @Test
    fun getCurrencyPairs() {
        cnbMockService.stubFor(
            WireMock.get("/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.xml")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBodyFile("cnb/denni_kurz.xml"),
                ),
        )
        currencyRateApiMockService.stubFor(
            get("/npm/@fawazahmed0/currency-api@latest/v1/currencies.json")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json", "charset=utf-8")
                        .withBodyFile("currency_api/currencies.json"),
                ),
        )

        val currencyPairsResultA = currencyExchangeService.getCurrencyPairs(
            CurrencyExchangeProviderId.CNB,
            CurrencyExchangeProviderId.CURRENCY_API
        )

        val currencyPairsResultB = currencyExchangeService.getCurrencyPairs(
            CurrencyExchangeProviderId.CURRENCY_API,
            CurrencyExchangeProviderId.CNB
        )

        assertThat(currencyPairsResultA)
            .hasSize(62)
            .containsExactlyInAnyOrderElementsOf(currencyPairsResultB)
    }

    @Test
    fun `calculateCurrencyExchangeRateDiff - Identical providers`() {
        assertThrows<IllegalArgumentException> {
            currencyExchangeService.calculateCurrencyExchangeRateDiff(
                CurrencyExchangeProviderId.CNB,
                CurrencyExchangeProviderId.CNB,
                Currencies.CZK,
                "USD",
            )
        }
    }

    @Test
    fun `calculateCurrencyExchangeRateDiff - Identical currencies`() {
        assertThrows<IllegalArgumentException> {
            currencyExchangeService.calculateCurrencyExchangeRateDiff(
                CurrencyExchangeProviderId.CNB,
                CurrencyExchangeProviderId.CURRENCY_API,
                Currencies.CZK,
                Currencies.CZK,
            )
        }
    }

    @Test
    fun calculateCurrencyExchangeRateDiff() {
        cnbMockService.stubFor(
            get("/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.xml")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBodyFile("cnb/denni_kurz.xml"),
                ),
        )
        currencyRateApiMockService.stubFor(
            get("/npm/@fawazahmed0/currency-api@latest/v1/currencies.json")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json", "charset=utf-8")
                        .withBodyFile("currency_api/currencies.json"),
                ),
        )
        currencyRateApiMockService.stubFor(
            get("/npm/@fawazahmed0/currency-api@latest/v1/currencies/czk.json")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json", "charset=utf-8")
                        .withBodyFile("currency_api/currency-czk.json"),
                ),
        )
        currencyRateApiMockService.stubFor(
            get("/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json", "charset=utf-8")
                        .withBodyFile("currency_api/currency-eur.json"),
                ),
        )

        val diffCzkEur = currencyExchangeService.calculateCurrencyExchangeRateDiff(
            CurrencyExchangeProviderId.CNB,
            CurrencyExchangeProviderId.CURRENCY_API,
            "CZK",
            "EUR",
        )
        val diffEurCzk = currencyExchangeService.calculateCurrencyExchangeRateDiff(
            CurrencyExchangeProviderId.CNB,
            CurrencyExchangeProviderId.CURRENCY_API,
            "EUR",
            "CZK",
        )

        assertThat(diffCzkEur).isEqualTo("-0.000024".toBigDecimal())
        assertThat(diffEurCzk).isEqualTo("0.014566".toBigDecimal())
    }
}