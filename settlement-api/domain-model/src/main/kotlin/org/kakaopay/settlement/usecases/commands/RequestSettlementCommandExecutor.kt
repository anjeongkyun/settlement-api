package org.kakaopay.settlement.usecases.commands

import org.kakaopay.settlement.Recipient
import org.kakaopay.settlement.SettlementStatus
import org.kakaopay.settlement.commands.RequestSettlementCommand
import org.kakaopay.settlement.entities.Settlement
import org.kakaopay.settlement.events.SettlementRequestedEvent
import org.kakaopay.settlement.exceptions.ErrorProperties
import org.kakaopay.settlement.exceptions.ErrorReason
import org.kakaopay.settlement.exceptions.InvalidRequestException
import org.kakaopay.settlement.gateways.UserGateway
import org.kakaopay.settlement.publisher.SettlementRequestedEventPublisher
import org.kakaopay.settlement.repositories.SettlementRepository

class RequestSettlementCommandExecutor(
    private val settlementRepository: SettlementRepository,
    private val settlementRequestedEventPublisher: SettlementRequestedEventPublisher,
    private val userGateway: UserGateway
) {
    fun execute(command: RequestSettlementCommand) {
        if (!userGateway.existsUsers(command.recipientIds)) {
            throw InvalidRequestException(
                errorProperties = listOf(
                    ErrorProperties(
                        "recipients.userId",
                        ErrorReason.NotFound
                    ),
                ),
                message = ""
            )
        }


        val createdSettlement = settlementRepository.create(
            Settlement(
                id = null,
                price = command.price,
                status = SettlementStatus.PENDING,
                requesterId = command.requesterId,
                recipients = command.recipientIds
                    .map {
                        Recipient(
                            userId = it,
                            isSettled = false
                        )
                    },
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
