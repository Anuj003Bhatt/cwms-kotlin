package com.bh.cwms.model.entity

import com.bh.cwms.model.dto.WalletDto
import com.bh.cwms.model.entity.base.DtoBridge
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
@Table(name = "wallets")
class Wallet (
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @UuidGenerator
    var id: UUID,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: User,

    @Column(name = "private_key", length = 3000)
    var privateKey: String? = null
) : DtoBridge<WalletDto> {

    @OneToMany(mappedBy = "wallet", cascade = [CascadeType.ALL])
    lateinit var walletItems: MutableList<WalletItem>

    override fun toDto(): WalletDto {
        return WalletDto(
            id = id,
            items = walletItems.map { it.toDto() }
        )
    }

}