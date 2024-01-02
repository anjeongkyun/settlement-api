package org.kakaopay.settlement.usecases.handlers

import org.kakaopay.settlement.events.PublishUnSettledUserEvent
import org.kakaopay.settlement.events.SettlementPendedEvent
import org.kakaopay.settlement.exceptions.ErrorProperties
import org.kakaopay.settlement.exceptions.ErrorReason
import org.kakaopay.settlement.exceptions.InvalidRequestException
import org.kakaopay.settlement.publisher.SettlementPendedEventPublisher
import org.kakaopay.settlement.repositories.SettlementRepository

class PublishUnSettledUserEventHandler(
    private val settlementRepository: SettlementRepository,
    private val settlementPendedEventPublisher: SettlementPendedEventPublisher
) {
    fun handle(event: PublishUnSettledUserEvent) {
        val settlement = settlementRepository.findById(event.settlementId)
            ?: throw InvalidRequestException(
                errorProperties = listOf(
                    ErrorProperties(
                        "settlementId",
                        ErrorReason.NotFound
                    ),
                ),
                message = ""
            )

        if (!settlement.recipients.all { it.isSettled }) {
            settlementPendedEventPublisher.publish(
                SettlementPendedEvent(
                    event.settlementId
                )
            )
        }
    }
}
