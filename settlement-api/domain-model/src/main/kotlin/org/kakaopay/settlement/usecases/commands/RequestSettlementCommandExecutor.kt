package org.kakaopay.settlement.usecases.commands

import org.kakaopay.settlement.PriceAmount
import org.kakaopay.settlement.Recipient
import org.kakaopay.settlement.SettlementStatus
import org.kakaopay.settlement.TransactionType
import org.kakaopay.settlement.commands.RequestSettlementCommand
import org.kakaopay.settlement.entities.Settlement
import org.kakaopay.settlement.entities.Transaction
import org.kakaopay.settlement.events.SettlementRequestedEvent
import org.kakaopay.settlement.exceptions.ErrorProperties
import org.kakaopay.settlement.exceptions.ErrorReason
import org.kakaopay.settlement.exceptions.InvalidRequestException
import org.kakaopay.settlement.gateways.UserGateway
import org.kakaopay.settlement.publisher.SettlementRequestedEventPublisher
import org.kakaopay.settlement.repositories.SettlementRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

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

        val recipients = makeRecipients(command)
        val createdSettlement = settlementRepository.create(
            Settlement(
                id = null,
                price = command.price,
                status = SettlementStatus.PENDING,
                requesterId = command.requesterId,
                recipients = recipients,
                transactions = listOf(
                    Transaction(
                        id = UUID.randomUUID().toString(),
                        userId = command.requesterId,
                        price = PriceAmount(
                            value = command.price.value / recipients.count(),
                            currency = command.price.currency
                        ),
                        type = TransactionType.SETTLEMENT,
                        createdDateTimeUtc = OffsetDateTime.now(ZoneOffset.UTC)
                    )
                ),
                createdDateTimeUtc = OffsetDateTime.now(ZoneOffset.UTC)
            )
        )

        publishSettlementRequestedEvent(createdSettlement.id!!)
    }

    private fun makeRecipients(command: RequestSettlementCommand) = listOf(
        Recipient(
            userId = command.requesterId,
            isSettled = true
        )
    ) + command.recipientIds
        .map {
            Recipient(
                userId = it,
                isSettled = false
            )
        }

    private fun publishSettlementRequestedEvent(settlementId: String) {
        settlementRequestedEventPublisher.publish(
            event = SettlementRequestedEvent(
                settlementId = settlementId
            )
        )
    }
}
