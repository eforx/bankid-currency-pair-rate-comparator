package com.efor.task.bankid.controller

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.ArraySchema

@Schema(description = "Response containing available currency pairs")
data class CurrencyPairsResponse(
    @ArraySchema(
        schema = Schema(implementation = CurrencyPair::class)
    )
    @Schema(description = "List of available currency pairs")
    val pairs: List<CurrencyPair>
)

@Schema(description = "Represents a currency exchange pair")
data class CurrencyPair(
    @Schema(description = "Source currency code", example = "USD")
    val source: String,

    @Schema(description = "Destination currency code", example = "EUR")
    val dest: String
)