package org.kakaopay.settlement

import java.time.OffsetDateTime

data class SettlementContract(
    val id: String,
    val price: PriceAmount,
    val status: SettlementStatus,
    val recipients: List<Recipient>,
    val createdDateTimeUtc: OffsetDateTime
)
