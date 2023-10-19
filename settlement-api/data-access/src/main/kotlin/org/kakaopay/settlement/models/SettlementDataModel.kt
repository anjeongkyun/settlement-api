package org.kakaopay.settlement.models

import org.kakaopay.settlement.*
import org.kakaopay.settlement.entities.Transaction
import org.springframework.data.mongodb.core.mapping.Document

@Document("settlements")
data class SettlementDataModel(
    val id: String,
    val price: PriceAmount,
    val status: SettlementStatus,
    val requesterId: String,
    val recipients: List<Recipient>,
    val transactions: List<Transaction>
)
