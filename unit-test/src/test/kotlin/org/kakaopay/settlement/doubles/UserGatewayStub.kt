package org.kakaopay.settlement.doubles

import org.kakaopay.settlement.gateways.UserGateway

class UserGatewayStub(
    private val userId: String
) : UserGateway {
    override fun existsUsers(userIds: List<String>): Boolean {
        return userIds.contains(userId)
    }
}
