package org.kakaopay.settlement.usecases.queries

import org.kakaopay.settlement.SettlementContract
import org.kakaopay.settlement.queries.GetSettlementForRequesterQuery
import org.kakaopay.settlement.queries.GetSettlementForRequesterQueryResponse
import org.kakaopay.settlement.repositories.SettlementRepository

class GetSettlementsForRequesterQueryProcessor(
    private val settlementRepository: SettlementRepository
) {
    fun process(
        query: GetSettlementForRequesterQuery
    ): GetSettlementForRequesterQueryResponse {
        return GetSettlementForRequesterQueryResponse(
            settlements = settlementRepository.getList(
                requesterId = query.requesterId
            )
                .map {
                    SettlementContract(
                        it.id!!,
                        it.price,
                        it.status,
                        it.recipients,
                        it.createdDateTimeUtc
                    )
                }
        )

    }
}
