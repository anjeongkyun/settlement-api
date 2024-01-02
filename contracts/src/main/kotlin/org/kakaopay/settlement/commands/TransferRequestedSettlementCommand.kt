package org.kakaopay.settlement.commands

import org.kakaopay.settlement.PriceAmount

data class TransferRequestedSettlementCommand(
    val settlementId: String,
    val userId: String,
    val price: PriceAmount
)
