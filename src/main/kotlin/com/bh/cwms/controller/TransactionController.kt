package com.bh.cwms.controller

import com.bh.cwms.model.dto.TransferRequest
import com.bh.cwms.model.dto.UserContext
import com.bh.cwms.service.transaction.TransactionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("transactions")
@Tag(name = "Transactions")
class TransactionController (
    private val transactionService: TransactionService
) {

    @PostMapping("transfer")
    @Operation(summary = "Transfer Units", description = "Transfer units of a currency to another wallet")
    fun transfer(
        @AuthenticationPrincipal context: UserContext,
        @RequestBody @Valid transferRequest: TransferRequest
    ) = transactionService.transferUnits(transferRequest, context.userId)
}