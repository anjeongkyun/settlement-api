package org.kakaopay.settlement.contexts

import org.kakaopay.settlement.publisher.SettlementRequestedEventPublisher
import org.kakaopay.settlement.repositories.SettlementRepository
import org.kakaopay.settlement.usecases.commands.RequestSettlementCommandExecutor
import org.kakaopay.settlement.usecases.commands.TransferRequestedSettlementCommandExecutor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UseCaseContext {

    @Bean
    open fun transferRequestedSettlementCommandExecutor(
        settlementRepository: SettlementRepository
    ): TransferRequestedSettlementCommandExecutor {
        return TransferRequestedSettlementCommandExecutor(settlementRepository)
    }

    @Bean
    open fun requestSettlementCommandExecutor(
        settlementRepository: SettlementRepository,
        SettlementRequestedEventPublisher: SettlementRequestedEventPublisher
    ): RequestSettlementCommandExecutor {
        return RequestSettlementCommandExecutor(
            settlementRepository,
            SettlementRequestedEventPublisher
        )
    }
}