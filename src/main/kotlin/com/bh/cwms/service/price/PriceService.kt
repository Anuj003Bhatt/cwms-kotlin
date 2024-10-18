package com.bh.cwms.service.price

import com.bh.cwms.api.PriceApi
import com.bh.cwms.model.type.Currency
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

interface PriceService {
    fun getPriceUsd(currency: Currency): BigDecimal
}

@Service
class PriceServiceImpl (
    private val api: PriceApi,
    private val mapper: ObjectMapper
) : PriceService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun getPriceUsd(currency: Currency): BigDecimal {

        val response: String? = api.getBitCoinPrice()?.toBlocking()?.value()
        // TODO: This can be replaced with a proper interface and module to fetch real time market prices
        response.let {
            val bpi = mapper.readValue(response, MutableMap::class.java)["bpi"] as MutableMap<*, *>
            return BigDecimal(
                ((bpi)["USD"] as MutableMap<*, *>)["rate_float"] as String
            )
        }
    }
}