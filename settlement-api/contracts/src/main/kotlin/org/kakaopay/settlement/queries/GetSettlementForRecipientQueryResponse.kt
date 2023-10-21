package org.kakaopay.settlement.queries

import org.kakaopay.settlement.SettlementContract

data class GetSettlementForRecipientQueryResponse(
    val settlements: List<SettlementContract>
)
