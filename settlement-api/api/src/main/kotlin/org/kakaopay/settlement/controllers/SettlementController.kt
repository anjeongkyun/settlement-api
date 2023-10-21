package org.kakaopay.settlement.controllers

import org.kakaopay.settlement.commands.RequestSettlementCommand
import org.kakaopay.settlement.commands.TransferRequestedSettlementCommand
import org.kakaopay.settlement.usecases.commands.RequestSettlementCommandExecutor
import org.kakaopay.settlement.usecases.commands.TransferRequestedSettlementCommandExecutor
import org.springframework.web.bind.annotation.*

@RestController
class SettlementController(
    private val requestedSettlementCommandExecutor: RequestSettlementCommandExecutor,
    private val transferRequestedSettlementCommandExecutor: TransferRequestedSettlementCommandExecutor
) {

    @PostMapping("/settlements/commands/request-settlement/execute")
    fun requestSettlement(
        @PathVariable("tenantId") tenantId: String,
        @RequestBody command: RequestSettlementCommand,
        @RequestHeader(value = "X-USER-ID", required = true) userId: String,
    ) {
        requestedSettlementCommandExecutor.execute(
            command.copy(requesterId = userId)
        )
    }

    @PostMapping("/settlements/commands/transfer-requested-settlement/execute")
    fun transferRequestedSettlement(
        @PathVariable("tenantId") tenantId: String,
        @RequestBody command: TransferRequestedSettlementCommand,
        @RequestHeader(value = "X-USER-ID", required = true) userId: String,
    ) {
        transferRequestedSettlementCommandExecutor.execute(
            command.copy(userId = userId)
        )
    }
}
