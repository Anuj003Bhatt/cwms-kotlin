package com.bh.cwms.model.dto

import com.bh.cwms.model.type.Currency
import java.math.BigDecimal
import java.util.UUID

data class WalletItemDto (
    var id: UUID,
    var currency: Currency,
    var balance: BigDecimal
)
