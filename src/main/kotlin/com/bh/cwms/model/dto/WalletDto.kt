package com.bh.cwms.model.dto

import lombok.Getter
import lombok.Setter
import java.math.BigDecimal
import java.util.UUID

@Getter
@Setter
data class WalletDto (
    var id:UUID,
    var items: List<WalletItemDto>,
    var balanceInUsd: BigDecimal = BigDecimal.ZERO,
    var publicKey: String? = null,
)