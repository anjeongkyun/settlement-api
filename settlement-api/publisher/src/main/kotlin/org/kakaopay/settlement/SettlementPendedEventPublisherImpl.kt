package org.kakaopay.settlement

import org.kakaopay.settlement.events.SettlementPendedEvent
import org.kakaopay.settlement.publisher.SettlementPendedEventPublisher

class SettlementPendedEventPublisherImpl : SettlementPendedEventPublisher {
    override fun publish(event: SettlementPendedEvent) {
        TODO("Not yet implemented")
    }
}
