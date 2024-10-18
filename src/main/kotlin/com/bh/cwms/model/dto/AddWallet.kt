package com.bh.cwms.model.dto

import com.bh.cwms.model.type.Currency
import jakarta.validation.constraints.NotBlank

data class AddWallet (
    var currency: Currency,
    @field:NotBlank(message = "Pin cannot be blank")
    var pin: String,
)

data class UpdateWallet (
    @field:NotBlank(message = "Old Pin cannot be blank.")
    var oldPin: String,
    @field:NotBlank(message = "New Pin cannot be blank.")
    var newPin: String,
    @field:NotBlank(message = "Public Key is required.")
    var publicKey: String,
)