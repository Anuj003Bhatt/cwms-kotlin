package com.bh.cwms.service.price

import com.bh.cwms.api.PriceApi
import com.bh.cwms.model.dto.Quote
import com.bh.cwms.model.dto.TickerResponse
import com.bh.cwms.model.type.Currency
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

interface PriceService {
    fun getPriceUsd(currency: Currency, denomination: String): List<TickerResponse>

    fun getPrice(currency: Currency, denomination: String): BigDecimal
}

@Service
class PriceServiceImpl (
    private val api: PriceApi,
    private val mapper: ObjectMapper
) : PriceService {
    companion object {
        private val log = LoggerFactory.getLogger(PriceServiceImpl::class.java)
    }

    private val idMap = mapOf(
        Currency.BITCOIN to "btc-bitcoin",
        Currency.ETHEREUM to "eth-ethereum",
    )

    override fun getPriceUsd(currency: Currency, denomination: String): List<TickerResponse> {
        log.debug("Retrieving quote for currency {} for denomination {}", currency, denomination)
        val response: String? = api.getQuotes(denomination)?.toBlocking()?.value()
        log.debug("Successfully retrieved quote for currency {} for denomination {}", currency, denomination)
        response.let {
            val typeRef = object: TypeReference<List<TickerResponse>>(){}
            return mapper.readValue(response, typeRef)

        }
    }

    override fun getPrice(currency: Currency, denomination: String): BigDecimal {
        log.info("GetPrice for $currency and denomination $denomination")
        val quotes = getPriceUsd(Currency.BITCOIN, denomination)
        val usdQuote: MutableMap.MutableEntry<String, Quote> = quotes.first {
            it.id == idMap[currency]
        }.quotes.entries.first {
                it.key == denomination
        }
        return BigDecimal.valueOf(usdQuote.value.price)
    }
}