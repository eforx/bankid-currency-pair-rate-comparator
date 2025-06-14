package com.efor.task.bankid.controller

data class CurrencyPairsResponse(
    val pairs: List<CurrencyPair>
)

data class CurrencyPair(
    val source: String,
    val dest: String
)