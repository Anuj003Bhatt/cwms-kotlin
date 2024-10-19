package com.bh.cwms.service.transaction

import com.bh.cwms.model.dto.Transaction
import com.bh.cwms.model.dto.TransferRequest
import com.bh.cwms.model.entity.Wallet
import com.bh.cwms.model.entity.WalletItem
import com.bh.cwms.repository.WalletItemRepository
import com.bh.cwms.repository.WalletRepository
import com.bh.cwms.util.EncryptionUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface TransactionService {
    fun transferUnits(transferRequest: TransferRequest, userId: UUID): Boolean
}

@Service
class TransactionServiceImpl (
    private val walletRepository: WalletRepository,
    private val walletItemRepository: WalletItemRepository
) : TransactionService {

    companion object {
        private val log = LoggerFactory.getLogger(TransactionServiceImpl::class.java)
    }

    @Transactional
    override fun transferUnits(transferRequest: TransferRequest, userId: UUID): Boolean {
        log.debug(
            "Processing transaction for user {} for currency {} for units {}",
            userId,
            transferRequest.currency,
            transferRequest.units
        )
        val sourceWallet = walletRepository.findByUserId(userId).orElseThrow {
            log.error("No wallet for user {} found", userId)
            RuntimeException("No wallet found for ID '$userId'")
        }
        val item: WalletItem =
            sourceWallet.walletItems.stream().filter { it.currency == transferRequest.currency }
                .findAny().orElseThrow {
                    log.error("Wallet Item for currency {} for wallet {} does not exist",
                        transferRequest.currency,
                        sourceWallet.id
                    )
                    RuntimeException("No wallet found for ID '$userId'")
                }
        if (transferRequest.units > item.balance) {
            throw RuntimeException("Insufficient Balance")
        }
        val privateKey: String = EncryptionUtil.decrypt(sourceWallet.privateKey!!, transferRequest.pin)
        val targetWallet: Wallet = walletRepository.findById(transferRequest.targetWalletId).orElseThrow {
            RuntimeException("No wallet found for ID '${transferRequest.targetWalletId}'")
        }
        val targetItem = targetWallet.walletItems.filter {
            it.currency == transferRequest.currency
        }.ifEmpty {
            throw RuntimeException("No wallet found for currency '${transferRequest.currency}'")
        }.first()

        val txn = Transaction(
            sourceWallet = sourceWallet.id,
            targetWallet = targetWallet.id,
            units = transferRequest.units
        )

        if(!EncryptionUtil.verifyKeyPair(transferRequest.publicKey, privateKey)) {
            throw RuntimeException("Invalid Access")
        }

        val mapper = ObjectMapper()
        txn.signature = EncryptionUtil.encrypt(mapper.writer().writeValueAsString(txn), privateKey)
        item.balance = item.balance.subtract(transferRequest.units)
        targetItem.balance = item.balance.add(transferRequest.units)
        walletItemRepository.save(item)
        walletItemRepository.save(targetItem)
        return true
    }

}