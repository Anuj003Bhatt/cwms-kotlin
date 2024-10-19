package com.bh.cwms.controller

import com.bh.cwms.model.dto.*
import com.bh.cwms.service.wallet.WalletService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("wallets")
@Tag(name = "Wallets")
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
        @PathVariable("id") id: UUID
    ) = walletService.getWallet(id, context.userId)

    @PostMapping
    @Operation(summary = "Create wallet for user", description = "Create a wallet for the user")
    @ApiResponses(value = [ApiResponse(responseCode = "201", description = "Wallet created successfully")])
    @ResponseStatus(HttpStatus.CREATED)
    fun createWallet(
        @RequestBody addWallet: AddWallet,
        @AuthenticationPrincipal context: UserContext
    ) = walletService.createWallet(addWallet, context.userId)

    @PostMapping("{id}/item")
    @Operation(summary = "Add a wallet item for user", description = "Add a wallet item for user")
    @ApiResponses(value = [ApiResponse(responseCode = "201", description = "Wallet Item added successfully")])
    @ResponseStatus(HttpStatus.CREATED)
    fun addWalletItem(
        @PathVariable("id") id: UUID,
        @RequestBody addWalletItem: AddWalletItem,
        @AuthenticationPrincipal context: UserContext
    ) = walletService.addWalletItem(id, addWalletItem, context.userId)

    @PatchMapping("{id}/pin/update")
    @Operation(summary = "Change Wallet Pin", description = "Change a wallet's pin")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "Pin changed successfully")])
    fun pinChange(
        @PathVariable("id") id: UUID,
        @RequestBody updateWallet: UpdateWallet,
        @AuthenticationPrincipal context: UserContext
    ) = walletService.updateWallet(updateWallet, id, context.userId)

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