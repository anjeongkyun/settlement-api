package org.kakaopay.settlement.usecases.commands

import autoparams.AutoSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.kakaopay.settlement.Recipient
import org.kakaopay.settlement.TestRepositoryContext
import org.kakaopay.settlement.doubles.SettlementPendedEventPublisherSpy
import org.kakaopay.settlement.entities.Settlement
import org.kakaopay.settlement.events.PublishUnSettledUserEvent
import org.kakaopay.settlement.exceptions.ErrorReason
import org.kakaopay.settlement.exceptions.InvalidRequestException
import org.kakaopay.settlement.repositories.SettlementRepository
import org.kakaopay.settlement.usecases.handlers.PublishUnSettledUserEventHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ContextConfiguration

@DataMongoTest
@ContextConfiguration(classes = [TestRepositoryContext::class])
class SpecsForPublishUnSettledUserEventHandler(
    @Autowired val settlementRepository: SettlementRepository
) {

    @ParameterizedTest
    @AutoSource
    fun sut_publishes_an_event_when_settlement_not_settled(
        settlement: Settlement,
        recipient1: Recipient,
        recipient2: Recipient,
        recipient3: Recipient
    ) {
        //Arrange
        val createdSettlement = settlementRepository.create(
            settlement.copy(
                id = null,
                recipients = listOf(
                    recipient1.copy(isSettled = true),
                    recipient2.copy(isSettled = true),
                    recipient3.copy(isSettled = false)
                )
            )
        )
        val settlementPendedEventPublisher = SettlementPendedEventPublisherSpy()
        val sut = PublishUnSettledUserEventHandler(
            settlementRepository,
            settlementPendedEventPublisher
        )

        val event = PublishUnSettledUserEvent(
            createdSettlement.id!!
        )

        //Act
        sut.handle(event)

        //Assert
        val actual = settlementPendedEventPublisher.getEvent()
        assertThat(actual).isNotNull
        assertThat(actual!!.settlementId).isEqualTo(createdSettlement.id)
    }

    @ParameterizedTest
    @AutoSource
    fun sut_throws_InvalidRequestException_when_settlementId_of_command_does_not_exist(
        settlementId: String
    ) {
        //Arrange
        val settlementPendedEventPublisher = SettlementPendedEventPublisherSpy()
        val sut = PublishUnSettledUserEventHandler(
            settlementRepository,
            settlementPendedEventPublisher
        )
        val event = PublishUnSettledUserEvent(settlementId)

        //Act
        var actual: InvalidRequestException? = null
        try {
            sut.handle(event)
        } catch (err: InvalidRequestException) {
            actual = err;
        }

        // Assert
        assertThat(actual).isNotNull
        assertThat(actual!!.errorProperties).isNotEmpty
        assertThat(actual!!.errorProperties[0].key)
            .isEqualTo("settlementId")
        assertThat(actual!!.errorProperties[0].errorReason)
            .isEqualTo(ErrorReason.NotFound)
    }
}
