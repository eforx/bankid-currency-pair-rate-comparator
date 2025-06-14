package com.efor.task.bankid.controller

import com.efor.task.bankid.providers.CurrencyExchangeProviderId
import com.efor.task.bankid.providers.CurrencyExchangeService
import com.efor.task.bankid.providers.normalizeCurrencyName
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/api/currency"],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Tag(name = "Currency Exchange", description = "Currency exchange rates and pairs API")
class CurrencyController(
    private val currencyExchangeService: CurrencyExchangeService
) {
    @Operation(
        summary = "Get available currency pairs",
        description = "Returns all available currency pairs between CNB and the specified provider"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "List of available currency pairs",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CurrencyPairsResponse::class)
                )]
            )
        ]
    )
    @GetMapping("/pairs", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAvailableCurrencyPairs(
        @Parameter(description = "Currency exchange provider ID", required = true)
        @RequestParam currencyExchangeProviderId: PublicCurrencyExchangeProviderId
    ): CurrencyPairsResponse {
        return currencyExchangeService.getCurrencyPairs(
            providerA = CurrencyExchangeProviderId.CNB,
            providerB = currencyExchangeProviderId.toInternal(),
        ).let { pairs ->
            CurrencyPairsResponse(pairs.map {
                CurrencyPair(
                    source = it.first,
                    dest = it.second
                )
            })
        }
    }

    @Operation(
        summary = "Get currency exchange rate difference",
        description = "Calculates the difference in exchange rates between CNB and the specified provider for a given currency pair"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Exchange rate difference calculated successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CurrencyExchangeRateDiffResponse::class)
                )]
            )
        ]
    )
    @GetMapping("/exchange-rate-diff")
    fun getCurrencyExchangeRateDiff(
        @Parameter(description = "Currency exchange provider ID to compare with CNB", required = true)
        @RequestParam currencyExchangeProviderId: PublicCurrencyExchangeProviderId,

        @Parameter(description = "Source currency code", example = "CZK", required = true)
        @RequestParam sourceCurrency: String,

        @Parameter(description = "Destination currency code", example = "EUR", required = true)
        @RequestParam destCurrency: String,
    ): CurrencyExchangeRateDiffResponse {
        return currencyExchangeService.calculateCurrencyExchangeRateDiff(
            sourceProvider = CurrencyExchangeProviderId.CNB,
            destProvider = currencyExchangeProviderId.toInternal(),
            sourceCurrency = sourceCurrency.normalizeCurrencyName(),
            destCurrency = destCurrency.normalizeCurrencyName(),
        ).let { CurrencyExchangeRateDiffResponse(it) }
    }
}