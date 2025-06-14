package com.efor.task.bankid.providers

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

fun String.normalizeCurrencyName(): String = this.uppercase()

val currencyRateMathContext = MathContext(12, RoundingMode.HALF_UP)

fun BigDecimal.normalizeCurrencyRate(): BigDecimal =
    this.setScale(
        6,
        currencyRateMathContext.roundingMode,
    )

fun BigDecimal.currencyDivide(divisor: Int): BigDecimal = this.divide(divisor.toBigDecimal(), currencyRateMathContext)

fun BigDecimal.currencyDivide(divisor: BigDecimal): BigDecimal = this.divide(divisor, currencyRateMathContext)

fun BigDecimal.currencyMultiply(divisor: Int): BigDecimal = this.multiply(divisor.toBigDecimal(), currencyRateMathContext)
