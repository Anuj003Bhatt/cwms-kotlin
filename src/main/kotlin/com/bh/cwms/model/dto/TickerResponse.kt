package com.bh.cwms.model.dto

data class TickerResponse (
    val id: String,
    val name: String,
    val symbol: String,
    val quotes: MutableMap<String, Quote>
)

data class Quote (
    val price: Double,
    val volume_24h: Double,
    val market_cap: Long,
)