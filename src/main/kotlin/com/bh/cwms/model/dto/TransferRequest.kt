package com.bh.cwms.model.dto

import com.bh.cwms.model.type.Currency
import java.math.BigDecimal
import java.util.UUID


data class TransferRequest (
    var sourceWalletId: UUID,
    val publicKey: String,
    val targetWalletId: UUID,
    val pin: String,
    val units: BigDecimal,
    val currency: Currency
)
