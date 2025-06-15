package com.efor.task.bankid.controller

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    CurrencyController::class,
)
class CurrencyControllerConfig
