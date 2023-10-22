package org.kakaopay.settlement.usecases.commands

import org.kakaopay.settlement.commands.PublishUnSettledUserCommand
import org.kakaopay.settlement.events.SettlementPendedEvent
import org.kakaopay.settlement.exceptions.ErrorProperties
import org.kakaopay.settlement.exceptions.ErrorReason
import org.kakaopay.settlement.exceptions.InvalidRequestException
import org.kakaopay.settlement.publisher.SettlementPendedEventPublisher
import org.kakaopay.settlement.repositories.SettlementRepository

class PublishUnSettledUserCommandExecutor(
    private val settlementRepository: SettlementRepository,
    private val settlementPendedEventPublisher: SettlementPendedEventPublisher
) {
    fun execute(command: PublishUnSettledUserCommand) {
        val settlement = settlementRepository.findById(command.settlementId)
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
                    command.settlementId
                )
            )
        }
    }
}
