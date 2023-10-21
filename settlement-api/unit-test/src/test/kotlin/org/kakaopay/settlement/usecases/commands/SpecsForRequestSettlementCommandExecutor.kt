package org.kakaopay.settlement.usecases.commands

import autoparams.AutoSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.kakaopay.settlement.PriceAmount
import org.kakaopay.settlement.Recipient
import org.kakaopay.settlement.SettlementStatus
import org.kakaopay.settlement.TestRepositoryContext
import org.kakaopay.settlement.commands.RequestSettlementCommand
import org.kakaopay.settlement.doubles.SettlementRequestedEventPublisherSpy
import org.kakaopay.settlement.events.SettlementRequestedEvent
import org.kakaopay.settlement.repositories.SettlementRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ContextConfiguration

@DataMongoTest
@ContextConfiguration(classes = [TestRepositoryContext::class])
class SpecsForRequestSettlementCommandExecutor(
    @Autowired val settlementRepository: SettlementRepository
) {

    @ParameterizedTest
    @AutoSource
    fun sut_creates_settlement_with_requested_command_correctly(
        requesterId: String,
        price: PriceAmount,
        recipients: List<Recipient>
    ) {
        //Arrange
        val sut = RequestSettlementCommandExecutor(
            settlementRepository,
            SettlementRequestedEventPublisherSpy()
        )

        val command = RequestSettlementCommand(
            requesterId = requesterId,
            price = price,
            recipients = recipients
        )

        //Act
        sut.execute(command)

        //Assert
        val actual = settlementRepository.getList(
            requesterId = requesterId
        )
            .firstOrNull()
        assertThat(actual).isNotNull
        assertThat(actual!!.requesterId).isEqualTo(requesterId)
        assertThat(actual!!.price).isEqualTo(price)
        assertThat(actual!!.recipients).isEqualTo(recipients)
    }

    @ParameterizedTest
    @AutoSource
    fun sut_creates_the_status_of_settlement_as_PENDING(
        requesterId: String,
        price: PriceAmount,
        recipients: List<Recipient>
    ) {
        //Arrange
        val sut = RequestSettlementCommandExecutor(
            settlementRepository,
            SettlementRequestedEventPublisherSpy()
        )

        val command = RequestSettlementCommand(
            requesterId = requesterId,
            price = price,
            recipients = recipients
        )

        //Act
        sut.execute(command)

        //Assert
        val actual = settlementRepository.getList(
            requesterId = requesterId
        ).first()
        assertThat(actual.status).isEqualTo(SettlementStatus.PENDING)
    }

    @ParameterizedTest
    @AutoSource
    fun sut_creates_transactions_as_empty_list(
        requesterId: String,
        price: PriceAmount,
        recipients: List<Recipient>
    ) {
        //Arrange
        val sut = RequestSettlementCommandExecutor(
            settlementRepository,
            SettlementRequestedEventPublisherSpy()
        )

        val command = RequestSettlementCommand(
            requesterId = requesterId,
            price = price,
            recipients = recipients
        )

        //Act
        sut.execute(command)

        //Assert
        val actual = settlementRepository.getList(
            requesterId = requesterId
        ).first()
        assertThat(actual.transactions).isEmpty()
    }

    @ParameterizedTest
    @AutoSource
    fun sut_publishes_an_event_when_settlement_correctly_is_created(
        requesterId: String,
        price: PriceAmount,
        recipients: List<Recipient>
    ) {
        //Arrange
        val publisher = SettlementRequestedEventPublisherSpy()
        val sut = RequestSettlementCommandExecutor(
            settlementRepository,
            publisher
        )

        val command = RequestSettlementCommand(
            requesterId = requesterId,
            price = price,
            recipients = recipients
        )

        //Act
        sut.execute(command)

        //Assert
        var actual = publisher.getEvent()
        assertThat(actual).isNotNull
        assertThat(actual).isInstanceOf(SettlementRequestedEvent::class.java)
        assertThat(actual!!.settlementId).isNotNull
    }
}
