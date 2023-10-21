package org.kakaopay.settlement

import org.kakaopay.settlement.events.SettlementRequestedEvent
import org.kakaopay.settlement.publisher.SettlementRequestedEventPublisher

class SettlementRequestedEventPublisherImpl : SettlementRequestedEventPublisher {
    override fun publish(event: SettlementRequestedEvent) {
        TODO("Not yet implemented")
    }
}
