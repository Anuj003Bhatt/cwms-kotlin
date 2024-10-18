package com.bh.cwms.model.dto

import java.util.UUID


data class UserDto (
    val id: UUID,
    val username: String,
)