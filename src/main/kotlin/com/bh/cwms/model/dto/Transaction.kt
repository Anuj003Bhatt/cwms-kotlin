package com.bh.cwms.model.dto

import java.math.BigDecimal
import java.util.*

data class Transaction (
    var sourceWallet: UUID,
    val targetWallet: UUID,
    val units: BigDecimal,
    var signature: String? = null
)
