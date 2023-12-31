package org.kakaopay.settlement.controllers

import org.kakaopay.settlement.commands.RequestSettlementApiCommand
import org.kakaopay.settlement.commands.RequestSettlementCommand
import org.kakaopay.settlement.commands.TransferRequestedSettlementApiCommand
import org.kakaopay.settlement.commands.TransferRequestedSettlementCommand
import org.kakaopay.settlement.events.PublishUnSettledUserEvent
import org.kakaopay.settlement.exceptions.InvalidRequestException
import org.kakaopay.settlement.exceptions.toHttpException
import org.kakaopay.settlement.queries.GetSettlementForRecipientQuery
import org.kakaopay.settlement.queries.GetSettlementForRecipientQueryResponse
import org.kakaopay.settlement.queries.GetSettlementForRequesterQuery
import org.kakaopay.settlement.queries.GetSettlementForRequesterQueryResponse
import org.kakaopay.settlement.usecases.commands.RequestSettlementCommandExecutor
import org.kakaopay.settlement.usecases.commands.TransferRequestedSettlementCommandExecutor
import org.kakaopay.settlement.usecases.handlers.PublishUnSettledUserEventHandler
import org.kakaopay.settlement.usecases.queries.GetSettlementsForRecipientQueryProcessor
import org.kakaopay.settlement.usecases.queries.GetSettlementsForRequesterQueryProcessor
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class SettlementController(
    private val requestedSettlementCommandExecutor: RequestSettlementCommandExecutor,
    private val transferRequestedSettlementCommandExecutor: TransferRequestedSettlementCommandExecutor,
    private val getSettlementsForRequesterQueryProcessor: GetSettlementsForRequesterQueryProcessor,
    private val getSettlementsForRecipientQueryProcessor: GetSettlementsForRecipientQueryProcessor,
    private val publishUnSettledUserEventHandler: PublishUnSettledUserEventHandler
) {

    @PostMapping("/settlements/commands/request-settlement")
    fun requestSettlement(
        @RequestBody command: RequestSettlementApiCommand,
        @RequestHeader(value = "X-USER-ID", required = true) userId: String,
    ) {
        try {
            requestedSettlementCommandExecutor.execute(
                RequestSettlementCommand(
                    requesterId = userId,
                    price = command.price,
                    recipientIds = command.recipientIds
                )
            )
        } catch (err: InvalidRequestException) {
            throw err.toHttpException(HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/settlements/commands/transfer-requested-settlement")
    fun transferRequestedSettlement(
        @RequestBody command: TransferRequestedSettlementApiCommand,
        @RequestHeader(value = "X-USER-ID", required = true) userId: String,
    ) {
        try {
            transferRequestedSettlementCommandExecutor.execute(
                TransferRequestedSettlementCommand(
                    settlementId = command.settlementId,
                    price = command.price,
                    userId = userId
                )
            )
        } catch (err: InvalidRequestException) {
            throw err.toHttpException(HttpStatus.BAD_REQUEST)
        }
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

    @PostMapping("/settlements/commands/publish-un-settled-user-event")
    fun publishUnSettledUserEvent(
        @RequestBody event: PublishUnSettledUserEvent
    ) {
        try {
            publishUnSettledUserEventHandler.handle(event = event)
        } catch (err: InvalidRequestException) {
            throw err.toHttpException(HttpStatus.BAD_REQUEST)
        }
    }
}
