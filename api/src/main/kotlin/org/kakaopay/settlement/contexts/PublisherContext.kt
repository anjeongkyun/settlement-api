package org.kakaopay.settlement.contexts

import org.kakaopay.settlement.SettlementPendedEventPublisherImpl
import org.kakaopay.settlement.SettlementRequestedEventPublisherImpl
import org.kakaopay.settlement.publisher.SettlementPendedEventPublisher
import org.kakaopay.settlement.publisher.SettlementRequestedEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PublisherContext {

    @Bean
    open fun settlementRequestedEventPublisher(): SettlementRequestedEventPublisher {
        return SettlementRequestedEventPublisherImpl()
    }

    @Bean
    open fun settlementPendedEventPublisher(): SettlementPendedEventPublisher {
        return SettlementPendedEventPublisherImpl()
    }
}
