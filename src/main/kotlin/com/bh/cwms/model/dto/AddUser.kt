package com.bh.cwms.model.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class AddUser (
    @NotEmpty(message = "Username cannot be empty")
    @Size(
        max = 50,
        message = "Username must be less than 50 characters"
    )
    var username:String,

    @NotEmpty(message = "password cannot be empty")
    @Size(
        min = 8,
        max = 50,
        message = "Password must be between 8-50 characters"
    )
    val password: String
)