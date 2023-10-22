package org.kakaopay.settlement.publisher

import org.kakaopay.settlement.events.SettlementPendedEvent

interface SettlementPendedEventPublisher {
    fun publish(event: SettlementPendedEvent)
}
