package com.efor.task.bankid.providers.cnb

import com.efor.task.bankid.providers.cnb.api.CnbCurrencyApi
import com.efor.task.bankid.providers.cnb.api.Row
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import org.wiremock.spring.InjectWireMock

@SpringBootTest(classes = [CnbConfig::class])
@EnableWireMock(
    value = [
        ConfigureWireMock(
            name = "cnb-service",
            baseUrlProperties = ["external.cnb.url"],
        ),
    ],
)
class DefaultCnbCurrencyApiTest(
    @Autowired private val cnbCurrencyApi: CnbCurrencyApi,
) {
    @InjectWireMock("cnb-service")
    private lateinit var cnbMockService: WireMockServer

    @Test
    fun fetchDailyExchangeRate() {
        cnbMockService.stubFor(
            WireMock.get("/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.xml")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBodyFile("cnb/denni_kurz.xml"),
                ),
        )

        val response = cnbCurrencyApi.fetchDailyExchangeRate()

        assertThat(response.table.rows)
            .hasSize(31)
            .contains(Row("USD", "dolar", 1, "21.667000".toBigDecimal(), "USA"))
    }
}