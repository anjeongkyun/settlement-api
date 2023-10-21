package org.kakaopay.settlement

data class SettlementContract(
    val id: String,
    val price: PriceAmount,
    val status: SettlementStatus,
    val recipients: List<Recipient>
)
