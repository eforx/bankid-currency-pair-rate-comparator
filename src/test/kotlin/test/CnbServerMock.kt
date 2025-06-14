package test

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock

class CnbServerMock(
    private val wireMock: WireMockServer
) {
    fun mockDailyExchangeRate() {
        wireMock.stubFor(
            WireMock.get("/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.xml")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBodyFile("cnb/denni_kurz.xml"),
                ),
        )
    }

    fun verifyDailyExchangeRateCall(count: Int = 1) {
        wireMock.verify(
            count,
            WireMock.getRequestedFor(
                WireMock.urlEqualTo("/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.xml")
            )
        )
    }
}