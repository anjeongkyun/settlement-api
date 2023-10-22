package org.kakaopay.settlement.repositories

import org.kakaopay.settlement.entities.Settlement

interface SettlementRepository {
    fun getList(
        requesterId: String? = null,
        recipientId: String? = null
    ): List<Settlement>

    fun create(settlement: Settlement): Settlement
    fun update(settlementId: String, modifier: (Settlement) -> Settlement)
    fun exists(settlementId: String): Boolean
}
