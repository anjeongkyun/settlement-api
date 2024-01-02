package org.kakaopay.settlement.entities

import org.kakaopay.settlement.PriceAmount
import org.kakaopay.settlement.TransactionType
import java.time.OffsetDateTime

data class Transaction(
    val id: String,
    val userId: String,
    val price: PriceAmount,
    val type: TransactionType,
    val createdDateTimeUtc: OffsetDateTime
)
