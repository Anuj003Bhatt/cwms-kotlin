package com.bh.cwms.controller

import com.bh.cwms.model.dto.AddUser
import com.bh.cwms.model.dto.UserAuthenticationRequest
import com.bh.cwms.service.user.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("users")
class UserController (
    private val userService: UserService
) {
    @PostMapping("signup")
    @Operation(summary = "Create new user", description = "Create a new user for the platform")
    @ApiResponses(value = [ApiResponse(responseCode = "201", description = "User added successfully")])
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirements
    fun addUser(
        @RequestBody @Valid addUser: AddUser
    ) = userService.createUser(addUser)

    @PostMapping("authenticate")
    @Operation(summary = "Authenticate a user by username and password and return a JWT")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Authentication successful"),
        ApiResponse(responseCode = "401", description = "Authentication failed")
    )
    @SecurityRequirements
    fun authenticate(
        @RequestBody @Valid authenticationRequest: UserAuthenticationRequest
    ): Map<String, String> = userService.authenticate(
        authenticationRequest.username,
        authenticationRequest.password
    )
}