package org.kakaopay.settlement.events

import com.fasterxml.jackson.annotation.JsonProperty

data class PublishUnSettledUserEvent(
    @JsonProperty("settlementId") val settlementId: String
)
