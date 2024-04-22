package com.digeltech.discountone.data.mapper

import com.digeltech.discountone.data.model.PriceChangeDto
import com.digeltech.discountone.data.model.PriceDto
import com.digeltech.discountone.domain.model.Price

class ParserMapper {
    fun mapPrices(data: PriceChangeDto): List<Price> = data.history.mapItems()

    private fun List<PriceDto>.mapItems(): List<Price> {
        return map { Price(it.price.toFloat(), it.date.toLong()) }
    }

}