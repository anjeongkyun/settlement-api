package org.kakaopay.settlement.publisher

import org.kakaopay.settlement.events.SettlementRequestedEvent

interface SettlementRequestedEventPublisher {
    fun publish(event: SettlementRequestedEvent)
}
