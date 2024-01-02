package org.kakaopay.settlement.contexts

import org.kakaopay.settlement.publisher.SettlementPendedEventPublisher
import org.kakaopay.settlement.repositories.SettlementRepository
import org.kakaopay.settlement.usecases.handlers.PublishUnSettledUserEventHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class EventHandlerContext {

    @Bean
    open fun publishUnSettledUserEventHandler(
        settlementRepository: SettlementRepository,
        settlementPendedEventPublisher: SettlementPendedEventPublisher
    ): PublishUnSettledUserEventHandler {
        return PublishUnSettledUserEventHandler(
            settlementRepository,
            settlementPendedEventPublisher
        )
    }
}
