package com.bh.cwms.service.wallet

import com.bh.cwms.model.dto.*
import com.bh.cwms.model.entity.Wallet
import com.bh.cwms.model.entity.WalletItem
import com.bh.cwms.repository.UserRepository
import com.bh.cwms.repository.WalletItemRepository
import com.bh.cwms.repository.WalletRepository
import com.bh.cwms.service.price.PriceService
import com.bh.cwms.util.EncryptionUtil
import com.bh.cwms.util.EncryptionUtil.encrypt
import com.bh.cwms.util.EncryptionUtil.generateKeyPair
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

interface WalletService {
    fun createWallet(newWallet: AddWallet, userId: UUID): WalletDto
    fun addWalletItem(walletId:UUID, newWallet: AddWallet, userId: UUID): WalletItemDto
    fun getWallet(id: UUID): WalletDto
    fun updateWallet(newWallet: UpdateWallet, id: UUID): WalletDto
    fun deleteWallet(id: UUID, userId:UUID, deleteWalletRequest: DeleteWalletRequest)
}

@Service
class WalletServiceImpl (
    private val priceService: PriceService,
    private val walletRepository: WalletRepository,
    private val walletItemRepository: WalletItemRepository,
    private val userRepository: UserRepository
) : WalletService {
    private val log = LoggerFactory.getLogger(this.javaClass);
    @Transactional
    override fun createWallet(newWallet: AddWallet, userId: UUID): WalletDto {
        val user = userRepository.findById(userId).orElseThrow {
            RuntimeException("No user for ID '${userId}' found")
        }

        walletRepository.findByUserId(userId).ifPresent {
            throw RuntimeException("Wallet for user '${userId}' already exists.")
        }

        val keyPair = generateKeyPair()
        val privateKey = Base64.getMimeEncoder().encodeToString(keyPair.private.encoded)

        val wallet = Wallet(
            id = UUID.randomUUID(),
            user = user,
            privateKey = encrypt(privateKey, newWallet.pin)
        )

        val walletItems = mutableListOf(
            WalletItem(
                id = UUID.randomUUID(),
                wallet = wallet,
                balance = BigDecimal.valueOf(100),
                currency = newWallet.currency
            )
        )
        wallet.walletItems = walletItems

        val walletDto = walletRepository.save(wallet).toDto()
        walletDto.publicKey = Base64.getMimeEncoder().encodeToString(keyPair.public.encoded)
        return walletDto
    }

    override fun addWalletItem(walletId: UUID, newWallet: AddWallet, userId: UUID): WalletItemDto {
        val wallet = walletRepository.findByUserId(userId).orElseThrow {
            RuntimeException("No wallet exists for user '${userId}'.")
        }
        if (wallet.walletItems.any { it.currency == newWallet.currency }) {
            throw RuntimeException("Wallet for currency '${newWallet.currency}' already exists.")
        }
        return walletItemRepository.save(
            WalletItem(
                id = UUID.randomUUID(),
                wallet = wallet,
                currency = newWallet.currency,
                balance = BigDecimal.ZERO
            )
        ).toDto()
    }

    private fun getWalletBalance(walletDto: WalletDto) : BigDecimal {
        val currencyPriceMap = mutableMapOf<String, BigDecimal>()
        val balance: BigDecimal = BigDecimal.ZERO
        walletDto.items.forEach {
            log.debug("Fetching price for ${it.currency.name}")
            val rate:BigDecimal = currencyPriceMap[it.currency.name] ?:priceService.getPriceUsd(it.currency)
            currencyPriceMap[it.currency.name] = rate
            balance.add(it.balance.multiply(rate))
        }
        return balance
    }

    override fun getWallet(id: UUID): WalletDto = walletRepository
        .findById(id)
        .orElseThrow {
            RuntimeException("No Wallet found for ID '${id}'")
        }.toDto().also {
            it.balanceInUsd = getWalletBalance(it)
        }

    override fun updateWallet(newWallet: UpdateWallet, userId: UUID): WalletDto {
        val wallet = walletRepository.findByUserId(userId).orElseThrow {
            RuntimeException("No wallet exists for user '${userId}'.")
        }
        val privateKey = EncryptionUtil.decrypt(wallet.privateKey!!, newWallet.oldPin)
        if(!EncryptionUtil.verifyKeyPair(newWallet.publicKey, privateKey)) {
            throw RuntimeException("Invalid Access Detected")
        }

        wallet.privateKey = EncryptionUtil.encrypt(privateKey, newWallet.newPin)
        return walletRepository.save(wallet).toDto()
    }

    override fun deleteWallet(id: UUID, userId: UUID, deleteWalletRequest: DeleteWalletRequest) {
        val wallet = walletRepository.findByUserId(userId).orElseThrow {
            RuntimeException("No wallet exists for user '${userId}'.")
        }
        val privateKey = EncryptionUtil.decrypt(wallet.privateKey!!, deleteWalletRequest.pin)
        if(!EncryptionUtil.verifyKeyPair(deleteWalletRequest.publicKey, privateKey)) {
            throw RuntimeException("Invalid Access Detected")
        }
        walletRepository.delete(wallet)
    }

}