package com.bh.cwms.model.type

import com.fasterxml.jackson.annotation.JsonCreator

enum class Currency {
    BITCOIN,
    ETHEREUM;

    @JsonCreator
    fun fromText(source: String): Currency {
        if (source.isBlank())
            throw IllegalArgumentException("Invalid Currency")
        return Currency.valueOf(source.trim().uppercase())
    }
}