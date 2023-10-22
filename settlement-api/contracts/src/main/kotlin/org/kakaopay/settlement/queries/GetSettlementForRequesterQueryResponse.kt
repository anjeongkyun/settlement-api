package org.kakaopay.settlement.queries

import org.kakaopay.settlement.SettlementContract

data class GetSettlementForRequesterQueryResponse(
    val settlements: List<SettlementContract>
)
