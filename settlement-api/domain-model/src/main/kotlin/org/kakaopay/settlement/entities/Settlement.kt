package org.kakaopay.settlement.entities

import org.kakaopay.settlement.*

data class Settlement(
    val id: String,
    val price: PriceAmount,
    val status: SettlementStatus,
    val requesterId: String,
    val recipients: List<Recipient>,
    val transactions: List<Transaction>
)
