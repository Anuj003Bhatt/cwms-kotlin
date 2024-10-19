package com.bh.cwms.model.type

import com.bh.cwms.exception.CwmsException
import com.fasterxml.jackson.annotation.JsonCreator

enum class Currency {
    BITCOIN,
    ETHEREUM;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    fun fromText(source: String): Currency {
        if (source.isBlank())
            throw CwmsException("Invalid Currency $source")
        return when(source.trim().uppercase()) {
            BITCOIN.name -> BITCOIN
            ETHEREUM.name -> ETHEREUM
            else -> throw CwmsException("Invalid Currency $source")
        }
    }
}