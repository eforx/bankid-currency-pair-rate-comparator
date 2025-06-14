package com.efor.task.bankid.providers.cnb

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("external.cnb")
data class CnbProperties(
    /**
     * CNB server base HTTP url
     */
    val url: String,
)
