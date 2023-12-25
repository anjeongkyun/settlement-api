package org.kakaopay.settlement.usecases.commands

import org.kakaopay.settlement.SettlementStatus
import org.kakaopay.settlement.TransactionType
import org.kakaopay.settlement.commands.TransferRequestedSettlementCommand
import org.kakaopay.settlement.entities.Settlement
import org.kakaopay.settlement.entities.Transaction
import org.kakaopay.settlement.exceptions.ErrorProperties
import org.kakaopay.settlement.exceptions.ErrorReason
import org.kakaopay.settlement.exceptions.InvalidRequestException
import org.kakaopay.settlement.repositories.SettlementRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class TransferRequestedSettlementCommandExecutor(
    private val settlementRepository: SettlementRepository,
) {
    fun execute(command: TransferRequestedSettlementCommand) {
        if (!settlementRepository.exists(settlementId = command.settlementId)) {
            throw InvalidRequestException(
                errorProperties = listOf(
                    ErrorProperties(
                        "settlementId",
                        ErrorReason.NotFound
                    ),
                ),
                message = ""
            )
        }

        settlementRepository.update(
            command.settlementId
        ) { settlement ->
            // 멱등성 보장을 위해 이미 정산된 유저일 경우, 그대로 반영 되도록 한다.
            if (settlement.recipients.first { it.userId == command.userId }.isSettled) {
                settlement
            } else {
                settlement.copy(
                    status = decideStatus(settlement, command),
                    recipients = settlement.recipients.map {
                        if (it.userId == command.userId) {
                            it.copy(isSettled = true)
                        } else {
                            it
                        }
                    },
                    transactions = settlement.transactions + listOf(
                        Transaction(
                            id = UUID.randomUUID().toString(),
                            userId = command.userId,
                            price = command.price,
                            createdDateTimeUtc = OffsetDateTime.now(ZoneOffset.UTC),
                            type = TransactionType.SETTLEMENT
                        )
                    )
                )
            }
        }
    }

    private fun decideStatus(
        settlement: Settlement,
        command: TransferRequestedSettlementCommand
    ) = when (settlement.price.value) {
        settlement.transactions.sumOf { it.price.value } + command.price.value
        -> SettlementStatus.SETTLED

        else -> SettlementStatus.PENDING
    }
}
