package com.efor.task.bankid.providers.cnb

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration of a CNB currency exchange rate provider
 */
@ConfigurationProperties("external.cnb")
data class CnbProperties(
    /**
     * CNB service base HTTP url
     */
    val url: String,
)
