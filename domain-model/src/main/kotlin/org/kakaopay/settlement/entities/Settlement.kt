package org.kakaopay.settlement.entities

import org.kakaopay.settlement.PriceAmount
import org.kakaopay.settlement.Recipient
import org.kakaopay.settlement.SettlementStatus
import java.time.OffsetDateTime

data class Settlement(
    val id: String?,
    val price: PriceAmount,
    val status: SettlementStatus,
    val requesterId: String,
    val recipients: List<Recipient>,
    val transactions: List<Transaction>,
    val createdDateTimeUtc: OffsetDateTime
)
