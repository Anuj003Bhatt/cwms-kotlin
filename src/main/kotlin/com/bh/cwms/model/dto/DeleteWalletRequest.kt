package com.bh.cwms.model.dto

data class DeleteWalletRequest (
    val pin: String,
    val publicKey: String
)