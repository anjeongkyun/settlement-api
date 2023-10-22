package org.kakaopay.settlement.doubles

import org.kakaopay.settlement.events.SettlementPendedEvent
import org.kakaopay.settlement.publisher.SettlementPendedEventPublisher

class SettlementPendedEventPublisherSpy(
    private var event: SettlementPendedEvent? = null
) : SettlementPendedEventPublisher {

    override fun publish(event: SettlementPendedEvent) {
        this.event = event
    }

    fun getEvent(): SettlementPendedEvent? {
        return this.event
    }
}
