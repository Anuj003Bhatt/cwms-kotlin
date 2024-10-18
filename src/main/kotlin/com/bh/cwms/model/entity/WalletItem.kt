package com.bh.cwms.model.entity

import com.bh.cwms.model.dto.WalletItemDto
import com.bh.cwms.model.entity.base.DtoBridge
import com.bh.cwms.model.type.Currency
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import lombok.Data
import org.hibernate.annotations.UuidGenerator
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "wallet_items")
@Data
data class WalletItem (
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @UuidGenerator
    var id: UUID,

    @ManyToOne(cascade = [CascadeType.ALL])
    var wallet:Wallet,

    @Column(name = "currency", nullable = false)
    val currency: Currency,

    @Column(name = "balance", nullable = false)
    var balance: BigDecimal = BigDecimal.ZERO

) : DtoBridge<WalletItemDto> {

    override fun toDto(): WalletItemDto {
        return WalletItemDto(
            id = id,
            currency = currency,
            balance = balance
        )
    }

}