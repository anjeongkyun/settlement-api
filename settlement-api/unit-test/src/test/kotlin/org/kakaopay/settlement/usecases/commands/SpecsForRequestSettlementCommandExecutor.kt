package org.kakaopay.settlement.usecases.commands

import autoparams.AutoSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.kakaopay.settlement.PriceAmount
import org.kakaopay.settlement.SettlementStatus
import org.kakaopay.settlement.TestRepositoryContext
import org.kakaopay.settlement.commands.RequestSettlementCommand
import org.kakaopay.settlement.doubles.SettlementRequestedEventPublisherSpy
import org.kakaopay.settlement.doubles.UserGatewayStub
import org.kakaopay.settlement.events.SettlementRequestedEvent
import org.kakaopay.settlement.exceptions.ErrorReason
import org.kakaopay.settlement.exceptions.InvalidRequestException
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
        recipientIds: List<String>
    ) {
        //Arrange
        val sut = RequestSettlementCommandExecutor(
            settlementRepository,
            SettlementRequestedEventPublisherSpy(),
            UserGatewayStub(recipientIds[0])
        )

        val command = RequestSettlementCommand(
            requesterId = requesterId,
            price = price,
            recipientIds = recipientIds
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
        assertThat(actual!!.recipients.map { it.userId }).isEqualTo(recipientIds)
        assertThat(actual!!.recipients.all { it.isSettled == false }).isTrue
    }

    @ParameterizedTest
    @AutoSource
    fun sut_creates_the_status_of_settlement_as_PENDING(
        requesterId: String,
        price: PriceAmount,
        recipientIds: List<String>
    ) {
        //Arrange
        val sut = RequestSettlementCommandExecutor(
            settlementRepository,
            SettlementRequestedEventPublisherSpy(),
            UserGatewayStub(recipientIds[0])
        )

        val command = RequestSettlementCommand(
            requesterId = requesterId,
            price = price,
            recipientIds = recipientIds
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
        recipientIds: List<String>
    ) {
        //Arrange
        val sut = RequestSettlementCommandExecutor(
            settlementRepository,
            SettlementRequestedEventPublisherSpy(),
            UserGatewayStub(recipientIds[0])
        )

        val command = RequestSettlementCommand(
            requesterId = requesterId,
            price = price,
            recipientIds = recipientIds
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
        recipientIds: List<String>
    ) {
        //Arrange
        val publisher = SettlementRequestedEventPublisherSpy()
        val sut = RequestSettlementCommandExecutor(
            settlementRepository,
            publisher,
            UserGatewayStub(recipientIds[0])
        )

        val command = RequestSettlementCommand(
            requesterId = requesterId,
            price = price,
            recipientIds = recipientIds
        )

        //Act
        sut.execute(command)

        //Assert
        var actual = publisher.getEvent()
        assertThat(actual).isNotNull
        assertThat(actual).isInstanceOf(SettlementRequestedEvent::class.java)
        assertThat(actual!!.settlementId).isNotNull
    }

    @ParameterizedTest
    @AutoSource
    fun sut_throws_InvalidRequestException_when_id_recipients_of_command_does_not_exists_in_accounts(
        requesterId: String,
        price: PriceAmount,
        recipientIds: List<String>,
        unSavedUserId: String
    ) {
        //Arrange
        val publisher = SettlementRequestedEventPublisherSpy()
        val sut = RequestSettlementCommandExecutor(
            settlementRepository,
            publisher,
            UserGatewayStub(unSavedUserId)
        )

        val command = RequestSettlementCommand(
            requesterId = requesterId,
            price = price,
            recipientIds = recipientIds
        )

        //Act
        var actual: InvalidRequestException? = null
        try {
            sut.execute(command)
        } catch (err: InvalidRequestException) {
            actual = err;
        }

        // Assert
        assertThat(actual).isNotNull
        assertThat(actual!!.errorProperties).isNotEmpty
        assertThat(actual!!.errorProperties[0].key)
            .isEqualTo("recipients.userId")
        assertThat(actual!!.errorProperties[0].errorReason)
            .isEqualTo(ErrorReason.NotFound)
    }
}
