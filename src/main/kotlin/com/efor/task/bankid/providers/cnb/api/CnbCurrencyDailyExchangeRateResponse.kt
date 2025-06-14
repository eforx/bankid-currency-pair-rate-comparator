package com.efor.task.bankid.providers.cnb.api

import com.efor.task.bankid.providers.currencyRateMathContext
import com.efor.task.bankid.providers.normalizeCurrencyRate
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.io.IOException
import java.math.BigDecimal

typealias CnbCurrencyDailyExchangeRateResponse = ExchangeRates

@JacksonXmlRootElement(localName = "kurzy")
data class ExchangeRates(
    @JacksonXmlProperty(isAttribute = true, localName = "banka")
    val bank: String,
    @JacksonXmlProperty(isAttribute = true, localName = "datum")
    val date: String,
    @JacksonXmlProperty(isAttribute = true, localName = "poradi")
    val order: Int,
    @JacksonXmlProperty(localName = "tabulka")
    val table: Table,
)

data class Table(
    @JacksonXmlProperty(isAttribute = true, localName = "typ")
    val type: String,
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "radek")
    val rows: List<Row> = emptyList(),
)

data class Row(
    @JacksonXmlProperty(isAttribute = true, localName = "kod")
    val code: String,
    @JacksonXmlProperty(isAttribute = true, localName = "mena")
    val currency: String,
    @JacksonXmlProperty(isAttribute = true, localName = "mnozstvi")
    val amount: Int,
    @JacksonXmlProperty(isAttribute = true, localName = "kurz")
    @JsonDeserialize(using = CnbBigDecimalDeserializer::class)
    val rate: BigDecimal,
    @JacksonXmlProperty(isAttribute = true, localName = "zeme")
    val country: String,
)

class CnbBigDecimalDeserializer : JsonDeserializer<BigDecimal?>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext?,
    ): BigDecimal? {
        return p.text
            ?.replace(",", ".")
            ?.toBigDecimal(currencyRateMathContext)
            ?.normalizeCurrencyRate()
    }
}
