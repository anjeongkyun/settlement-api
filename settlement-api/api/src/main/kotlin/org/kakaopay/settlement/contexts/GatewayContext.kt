package org.kakaopay.settlement.contexts

import org.kakaopay.settlement.UserGatewayImpl
import org.kakaopay.settlement.gateways.UserGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class GatewayContext {

    @Bean
    open fun userGateway(): UserGateway {
        return UserGatewayImpl()
    }
}
