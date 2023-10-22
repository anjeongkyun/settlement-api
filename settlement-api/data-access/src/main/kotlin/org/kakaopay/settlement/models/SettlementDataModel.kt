package org.kakaopay.settlement.models

import org.bson.types.ObjectId
import org.kakaopay.settlement.*
import org.kakaopay.settlement.entities.Transaction
import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime

@Document("settlements")
data class SettlementDataModel(
    val id: ObjectId?,
    val price: PriceAmount,
    val status: SettlementStatus,
    val requesterId: String,
    val recipients: List<Recipient>,
    val transactions: List<Transaction>,
    val createdDateTimeUtc: OffsetDateTime
)
