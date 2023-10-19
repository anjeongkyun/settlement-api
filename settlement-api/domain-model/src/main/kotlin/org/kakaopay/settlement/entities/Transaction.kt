package org.kakaopay.settlement.entities

import org.kakaopay.settlement.PriceAmount
import org.kakaopay.settlement.SettlementTransactionStatus
import java.time.OffsetDateTime

data class Transaction(
    val id: String,
    val price: PriceAmount,
    val status: SettlementTransactionStatus,
    val createdDateTimeUtc: OffsetDateTime
)
