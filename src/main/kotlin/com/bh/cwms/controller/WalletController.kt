package com.bh.cwms.controller

import com.bh.cwms.model.dto.AddWallet
import com.bh.cwms.model.dto.DeleteWalletRequest
import com.bh.cwms.model.dto.UserContext
import com.bh.cwms.service.wallet.WalletService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("wallets")
class WalletController (
    private val walletService: WalletService
) {
    @GetMapping("{id}")
    @Operation(summary = "Fetch wallet by ID", description = "Find a wallet by its ID")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Found wallet by ID"
        ), ApiResponse(
            responseCode = "404",
            description = "No wallet found for given ID"
        )]
    )
    fun findWalletById(
        @AuthenticationPrincipal context: UserContext,
        @PathVariable("id") id: UUID?
    ) = walletService.getWallet(context.userId)

    @PostMapping
    @Operation(summary = "Create wallet for user", description = "Create a wallet for the user")
    @ApiResponses(value = [ApiResponse(responseCode = "201", description = "Wallet created successfully")])
    @ResponseStatus(HttpStatus.CREATED)
    fun createWallet(
        @RequestBody addWallet: AddWallet,
        @AuthenticationPrincipal context: UserContext
    ) = walletService.createWallet(addWallet, context.userId)

    @DeleteMapping("{id}")
    @Operation(summary = "Delete wallet", description = "Delete a wallet for the user")
    @ApiResponses(value = [ApiResponse(responseCode = "201", description = "Wallet created successfully")])
    fun deleteWallet(
        @PathVariable("id") walletId: UUID,
        @RequestBody @Valid deleteWalletRequest: DeleteWalletRequest,
        @AuthenticationPrincipal context: UserContext
    ): Boolean {
        walletService.deleteWallet(walletId, context.userId, deleteWalletRequest)
        return true
    }

}