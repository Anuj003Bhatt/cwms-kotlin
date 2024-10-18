package com.bh.cwms.repository

import com.bh.cwms.model.entity.Wallet
import com.bh.cwms.model.entity.WalletItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface WalletRepository : JpaRepository<Wallet, UUID> {
    fun findByUserId(userId: UUID): Optional<Wallet>
}

@Repository
interface WalletItemRepository : JpaRepository<WalletItem, UUID>