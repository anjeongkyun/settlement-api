package org.kakaopay.settlement.usecases.commands

import org.kakaopay.settlement.SettlementStatus
import org.kakaopay.settlement.commands.RequestSettlementCommand
import org.kakaopay.settlement.entities.Settlement
import org.kakaopay.settlement.events.SettlementRequestedEvent
import org.kakaopay.settlement.publisher.SettlementRequestedEventPublisher
import org.kakaopay.settlement.repositories.SettlementRepository

class RequestSettlementCommandExecutor(
    private val settlementRepository: SettlementRepository,
    private val settlementRequestedEventPublisher: SettlementRequestedEventPublisher
) {
    fun execute(command: RequestSettlementCommand) {
        val createdSettlement = settlementRepository.create(
            Settlement(
                id = null,
                price = command.price,
                status = SettlementStatus.PENDING,
                requesterId = command.requesterId,
                recipients = command.recipients,
                transactions = emptyList()
            )
        )
        
        publishSettlementRequestedEvent(createdSettlement.id!!)
    }

    private fun publishSettlementRequestedEvent(settlementId: String) {
        settlementRequestedEventPublisher.publish(
            event = SettlementRequestedEvent(
                settlementId = settlementId
            )
        )
    }
}
