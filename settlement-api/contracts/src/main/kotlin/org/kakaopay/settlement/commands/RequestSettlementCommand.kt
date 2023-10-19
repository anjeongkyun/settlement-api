package org.kakaopay.settlement.commands

import org.kakaopay.settlement.PriceAmount
import org.kakaopay.settlement.Recipient

data class RequestSettlementCommand(
    val requesterId: String,
    val price: PriceAmount,
    val recipients: List<Recipient>
)
