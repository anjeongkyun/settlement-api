package org.kakaopay.settlement.usecases.commands

import org.kakaopay.settlement.SettlementStatus
import org.kakaopay.settlement.TransactionType
import org.kakaopay.settlement.commands.TransferRequestedSettlementCommand
import org.kakaopay.settlement.entities.Transaction
import org.kakaopay.settlement.repositories.SettlementRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

//TODO: status 만드는 코드 리팩터링
class TransferRequestedSettlementCommandExecutor(
    private val settlementRepository: SettlementRepository,
) {
    fun execute(command: TransferRequestedSettlementCommand) {
        settlementRepository.update(
            command.settlementId
        ) { settlement ->
            settlement.copy(
                status = SettlementStatus.SETTLED
                    .takeIf {
                        settlement.recipients.count { !it.isSettled } == 1
                                && settlement.recipients.any { it.userId == command.userId && !it.isSettled }
                    } ?: SettlementStatus.PENDING,
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
                        price = command.price,
                        createdDateTimeUtc = OffsetDateTime.now(ZoneOffset.UTC),
                        type = TransactionType.SETTLEMENT
                    )
                )
            )
        }


    }
}
