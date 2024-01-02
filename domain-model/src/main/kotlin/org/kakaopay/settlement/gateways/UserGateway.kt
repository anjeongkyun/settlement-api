package org.kakaopay.settlement.gateways

interface UserGateway {
    fun existsUsers(userIds: List<String>): Boolean
}
