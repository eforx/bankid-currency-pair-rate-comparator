package com.efor.task.bankid.controller

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "Response containing the difference between exchange rates from different providers")
data class CurrencyExchangeRateDiffResponse(
    @Schema(description = "The difference between exchange rates", example = "0.0123")
    val exchangeRateDiff: BigDecimal,
)
