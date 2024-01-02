package org.kakaopay.settlement.commands

import org.kakaopay.settlement.PriceAmount

data class TransferRequestedSettlementApiCommand(
    val settlementId: String,
    val price: PriceAmount
)
