package test

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get

class CurrencyApiServerMock(
    private val wireMock: WireMockServer
) {
    fun mockCurrencyList() {
        wireMock.stubFor(
            get("/npm/@fawazahmed0/currency-api@latest/v1/currencies.json")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json", "charset=utf-8")
                        .withBodyFile("currency_api/currencies.json"),
                ),
        )
    }

    fun mockCzkExchangeRate() {
        wireMock.stubFor(
            get("/npm/@fawazahmed0/currency-api@latest/v1/currencies/czk.json")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json", "charset=utf-8")
                        .withBodyFile("currency_api/currency-czk.json"),
                ),
        )
    }

    fun mockEurExchangeRate() {
        wireMock.stubFor(
            get("/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json", "charset=utf-8")
                        .withBodyFile("currency_api/currency-eur.json"),
                ),
        )
    }

    fun mockJpyExchangeRate() {
        wireMock.stubFor(
            get("/npm/@fawazahmed0/currency-api@latest/v1/currencies/jpy.json")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json", "charset=utf-8")
                        .withBodyFile("currency_api/currency-jpy.json"),
                ),
        )
    }

    fun verifyCurrencyListCall(count: Int = 1) {
        wireMock.verify(
            count,
            WireMock.getRequestedFor(
                WireMock.urlEqualTo("/npm/@fawazahmed0/currency-api@latest/v1/currencies.json")
            )
        )
    }

    fun verifyCzkExchangeRateCall(count: Int = 1) {
        wireMock.verify(
            count,
            WireMock.getRequestedFor(
                WireMock.urlEqualTo("/npm/@fawazahmed0/currency-api@latest/v1/currencies/czk.json")
            )
        )
    }

    fun verifyEurExchangeRateCall(count: Int = 1) {
        wireMock.verify(
            count,
            WireMock.getRequestedFor(
                WireMock.urlEqualTo("/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json")
            )
        )
    }

    fun verifyJpyExchangeRateCall(count: Int = 1) {
        wireMock.verify(
            count,
            WireMock.getRequestedFor(
                WireMock.urlEqualTo("/npm/@fawazahmed0/currency-api@latest/v1/currencies/jpy.json")
            )
        )
    }
}