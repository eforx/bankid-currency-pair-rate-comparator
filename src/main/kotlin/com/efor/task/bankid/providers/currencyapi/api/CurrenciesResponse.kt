package com.efor.task.bankid.providers.currencyapi.api

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter

data class CurrenciesResponse(
    private val names: MutableMap<String, String> = mutableMapOf(),
) {
    @JsonAnyGetter
    fun getNames(): Map<String, String> = names

    @JsonAnySetter
    fun setName(
        key: String,
        value: String,
    ) {
        names[key] = value
    }
}
