package com.efor.task.bankid.providers

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import org.wiremock.spring.InjectWireMock
import test.CnbServerMock
import test.CurrencyApiServerMock

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
    fun getCurrencyPairs() {
        cnbServerMock.mockDailyExchangeRate()
        currencyApiServerMock.mockCurrencyList()

        val currencyPairsResultA = currencyExchangeService.getCurrencyPairs(
            CurrencyExchangeProviderId.CNB,
            CurrencyExchangeProviderId.CURRENCY_API
        )

        val currencyPairsResultB = currencyExchangeService.getCurrencyPairs(
            CurrencyExchangeProviderId.CURRENCY_API,
            CurrencyExchangeProviderId.CNB
        )

        cnbServerMock.verifyDailyExchangeRateCall(2)
        currencyApiServerMock.verifyCurrencyListCall(2)

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
                "CZK",
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
                "CZK",
                "CZK",
            )
        }
    }

    @Test
    fun `calculateCurrencyExchangeRateDiff - Unknown currency`() {
        cnbServerMock.mockDailyExchangeRate()
        currencyApiServerMock.mockCurrencyList()

        assertThrows<IllegalArgumentException> {
            currencyExchangeService.calculateCurrencyExchangeRateDiff(
                CurrencyExchangeProviderId.CNB,
                CurrencyExchangeProviderId.CURRENCY_API,
                "awesome currency",
                "CZK",
            )
        }

        cnbServerMock.verifyDailyExchangeRateCall()
        currencyApiServerMock.verifyCurrencyListCall()
    }

    @Test
    fun `calculateCurrencyExchangeRateDiff - CZK-EUR`() {
        cnbServerMock.mockDailyExchangeRate()
        currencyApiServerMock.mockCurrencyList()
        currencyApiServerMock.mockCzkExchangeRate()
        currencyApiServerMock.mockEurExchangeRate()

        val diffCzkEurA = currencyExchangeService.calculateCurrencyExchangeRateDiff(
            CurrencyExchangeProviderId.CNB,
            CurrencyExchangeProviderId.CURRENCY_API,
            "CZK",
            "EUR",
        )
        val diffEurCzkA = currencyExchangeService.calculateCurrencyExchangeRateDiff(
            CurrencyExchangeProviderId.CNB,
            CurrencyExchangeProviderId.CURRENCY_API,
            "EUR",
            "CZK",
        )
        val diffCzkEurB = currencyExchangeService.calculateCurrencyExchangeRateDiff(
            CurrencyExchangeProviderId.CURRENCY_API,
            CurrencyExchangeProviderId.CNB,
            "CZK",
            "EUR",
        )
        val diffEurCzkB = currencyExchangeService.calculateCurrencyExchangeRateDiff(
            CurrencyExchangeProviderId.CURRENCY_API,
            CurrencyExchangeProviderId.CNB,
            "EUR",
            "CZK",
        )

        cnbServerMock.verifyDailyExchangeRateCall(8)
        currencyApiServerMock.verifyCurrencyListCall(4)
        currencyApiServerMock.verifyCzkExchangeRateCall(2)
        currencyApiServerMock.verifyEurExchangeRateCall(2)

        assertThat(diffCzkEurA).isEqualTo("-0.000024".toBigDecimal())
        assertThat(diffEurCzkA).isEqualTo("0.014566".toBigDecimal())
        assertThat(diffCzkEurB).isEqualTo("0.000024".toBigDecimal())
        assertThat(diffEurCzkB).isEqualTo("-0.014566".toBigDecimal())
    }

    @Test
    fun `calculateCurrencyExchangeRateDiff - CZK-JPY`() {
        cnbServerMock.mockDailyExchangeRate()
        currencyApiServerMock.mockCurrencyList()
        currencyApiServerMock.mockCzkExchangeRate()
        currencyApiServerMock.mockJpyExchangeRate()

        val diffCzkJpyA = currencyExchangeService.calculateCurrencyExchangeRateDiff(
            CurrencyExchangeProviderId.CNB,
            CurrencyExchangeProviderId.CURRENCY_API,
            "CZK",
            "JPY",
        )
        val diffJpyCzkA = currencyExchangeService.calculateCurrencyExchangeRateDiff(
            CurrencyExchangeProviderId.CNB,
            CurrencyExchangeProviderId.CURRENCY_API,
            "JPY",
            "CZK",
        )
        val diffCzkJpyB = currencyExchangeService.calculateCurrencyExchangeRateDiff(
            CurrencyExchangeProviderId.CURRENCY_API,
            CurrencyExchangeProviderId.CNB,
            "CZK",
            "JPY",
        )
        val diffJpyCzkB = currencyExchangeService.calculateCurrencyExchangeRateDiff(
            CurrencyExchangeProviderId.CURRENCY_API,
            CurrencyExchangeProviderId.CNB,
            "JPY",
            "CZK",
        )

        cnbServerMock.verifyDailyExchangeRateCall(8)
        currencyApiServerMock.verifyCurrencyListCall(4)
        currencyApiServerMock.verifyCzkExchangeRateCall(2)
        currencyApiServerMock.verifyJpyExchangeRateCall(2)

        assertThat(diffCzkJpyA).isEqualTo("0.024804".toBigDecimal())
        assertThat(diffJpyCzkA).isEqualTo("-0.000081".toBigDecimal())
        assertThat(diffCzkJpyB).isEqualTo("-0.024804".toBigDecimal())
        assertThat(diffJpyCzkB).isEqualTo("0.000081".toBigDecimal())
    }
}