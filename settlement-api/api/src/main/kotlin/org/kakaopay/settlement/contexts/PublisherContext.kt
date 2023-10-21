package org.kakaopay.settlement.contexts

import org.kakaopay.settlement.SettlementRequestedEventPublisherImpl
import org.kakaopay.settlement.publisher.SettlementRequestedEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PublisherContext {

    @Bean
    open fun SettlementRequestedEventPublisher(): SettlementRequestedEventPublisher {
        return SettlementRequestedEventPublisherImpl()
    }
}
