package org.kakaopay.settlement.doubles

import org.kakaopay.settlement.events.SettlementRequestedEvent
import org.kakaopay.settlement.publisher.SettlementRequestedEventPublisher

class SettlementRequestedEventPublisherSpy(
    private var event: SettlementRequestedEvent? = null
) : SettlementRequestedEventPublisher {

    override fun publish(event: SettlementRequestedEvent) {
        this.event = event
    }

    fun getEvent(): SettlementRequestedEvent? {
        return this.event
    }
}
