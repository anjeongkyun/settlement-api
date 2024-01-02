package org.kakaopay.settlement.commands

import org.kakaopay.settlement.PriceAmount

data class RequestSettlementApiCommand(
    val price: PriceAmount,
    val recipientIds: List<String>
)
