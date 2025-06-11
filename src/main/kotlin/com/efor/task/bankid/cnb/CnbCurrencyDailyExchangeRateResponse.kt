package com.efor.task.bankid.cnb

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.io.IOException
import java.math.BigDecimal

typealias CnbCurrencyDailyExchangeRateResponse = Kurzy

@JacksonXmlRootElement(localName = "kurzy")
data class Kurzy(
    @JacksonXmlProperty(isAttribute = true, localName = "banka")
    val banka: String,
    @JacksonXmlProperty(isAttribute = true, localName = "datum")
    val datum: String,
    @JacksonXmlProperty(isAttribute = true, localName = "poradi")
    val poradi: Int,
    @JacksonXmlProperty(localName = "tabulka")
    val tabulka: Tabulka,
)

data class Tabulka(
    @JacksonXmlProperty(isAttribute = true, localName = "typ")
    val typ: String,
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "radek")
    val radky: List<Radek> = emptyList(),
)

data class Radek(
    @JacksonXmlProperty(isAttribute = true, localName = "kod")
    val kod: String,
    @JacksonXmlProperty(isAttribute = true, localName = "mena")
    val mena: String,
    @JacksonXmlProperty(isAttribute = true, localName = "mnozstvi")
    val mnozstvi: Int,
    @JacksonXmlProperty(isAttribute = true, localName = "kurz")
    @JsonDeserialize(using = CnbBigDecimalDeserializer::class)
    val kurz: BigDecimal,
    @JacksonXmlProperty(isAttribute = true, localName = "zeme")
    val zeme: String,
)

class CnbBigDecimalDeserializer : JsonDeserializer<BigDecimal?>() {
    @Throws(IOException::class)
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext?,
    ): BigDecimal? {
        return p.text
            ?.replace(",", ".")
            ?.toBigDecimal()
    }
}
