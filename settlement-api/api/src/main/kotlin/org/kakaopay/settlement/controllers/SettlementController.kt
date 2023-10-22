package org.kakaopay.settlement.controllers

import org.kakaopay.settlement.commands.RequestSettlementCommand
import org.kakaopay.settlement.commands.TransferRequestedSettlementCommand
import org.kakaopay.settlement.queries.GetSettlementForRecipientQuery
import org.kakaopay.settlement.queries.GetSettlementForRecipientQueryResponse
import org.kakaopay.settlement.queries.GetSettlementForRequesterQuery
import org.kakaopay.settlement.queries.GetSettlementForRequesterQueryResponse
import org.kakaopay.settlement.usecases.commands.RequestSettlementCommandExecutor
import org.kakaopay.settlement.usecases.commands.TransferRequestedSettlementCommandExecutor
import org.kakaopay.settlement.usecases.queries.GetSettlementsForRecipientQueryProcessor
import org.kakaopay.settlement.usecases.queries.GetSettlementsForRequesterQueryProcessor
import org.springframework.web.bind.annotation.*

@RestController
class SettlementController(
    private val requestedSettlementCommandExecutor: RequestSettlementCommandExecutor,
    private val transferRequestedSettlementCommandExecutor: TransferRequestedSettlementCommandExecutor,
    private val getSettlementsForRequesterQueryProcessor: GetSettlementsForRequesterQueryProcessor,
    private val getSettlementsForRecipientQueryProcessor: GetSettlementsForRecipientQueryProcessor
) {

    @PostMapping("/settlements/commands/request-settlement")
    fun requestSettlement(
        @RequestBody command: RequestSettlementCommand,
        @RequestHeader(value = "X-USER-ID", required = true) userId: String,
    ) {
        requestedSettlementCommandExecutor.execute(
            command.copy(requesterId = userId)
        )
    }

    @PostMapping("/settlements/commands/transfer-requested-settlement")
    fun transferRequestedSettlement(
        @RequestBody command: TransferRequestedSettlementCommand,
        @RequestHeader(value = "X-USER-ID", required = true) userId: String,
    ) {
        transferRequestedSettlementCommandExecutor.execute(
            command.copy(userId = userId)
        )
    }

    @GetMapping("/settlements/queries/get-settlements-for-requester")
    fun getSettlementsForRequester(
        @RequestHeader(value = "X-USER-ID", required = true) userId: String,
    ): GetSettlementForRequesterQueryResponse {
        return getSettlementsForRequesterQueryProcessor.process(
            query = GetSettlementForRequesterQuery(
                requesterId = userId
            )
        )
    }

    @GetMapping("/settlements/queries/get-settlements-for-recipient")
    fun getSettlementsForRecipient(
        @RequestHeader(value = "X-USER-ID", required = true) userId: String,
    ): GetSettlementForRecipientQueryResponse {
        return getSettlementsForRecipientQueryProcessor.process(
            query = GetSettlementForRecipientQuery(
                recipientId = userId
            )
        )
    }
}
