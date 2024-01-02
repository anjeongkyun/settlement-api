package org.kakaopay.settlement.commands

import org.kakaopay.settlement.PriceAmount

data class RequestSettlementCommand(
    val requesterId: String,
    val price: PriceAmount,
    val recipientIds: List<String>
)
