package org.kakaopay.settlement.usecases.queries

import org.kakaopay.settlement.SettlementContract
import org.kakaopay.settlement.queries.GetSettlementForRecipientQuery
import org.kakaopay.settlement.queries.GetSettlementForRecipientQueryResponse
import org.kakaopay.settlement.repositories.SettlementRepository

class GetSettlementsForRecipientQueryProcessor(
    private val settlementRepository: SettlementRepository
) {
    fun process(
        query: GetSettlementForRecipientQuery
    ): GetSettlementForRecipientQueryResponse {
        return GetSettlementForRecipientQueryResponse(
            settlements = settlementRepository.getList(
                recipientId = query.recipientId
            )
                .map {
                    SettlementContract(
                        it.id!!,
                        it.price,
                        it.status,
                        it.recipients
                    )
                }
        )
    }
}
