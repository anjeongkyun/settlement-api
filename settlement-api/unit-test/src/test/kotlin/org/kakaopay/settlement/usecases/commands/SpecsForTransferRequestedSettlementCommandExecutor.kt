package org.kakaopay.settlement.usecases.commands

import autoparams.AutoSource
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.params.ParameterizedTest
import org.kakaopay.settlement.*
import org.kakaopay.settlement.commands.TransferRequestedSettlementCommand
import org.kakaopay.settlement.entities.Settlement
import org.kakaopay.settlement.exceptions.ErrorReason
import org.kakaopay.settlement.exceptions.InvalidRequestException
import org.kakaopay.settlement.repositories.SettlementRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ContextConfiguration
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@DataMongoTest
@ContextConfiguration(classes = [TestRepositoryContext::class])
class SpecsForTransferRequestedSettlementCommandExecutor(
    @Autowired val settlementRepository: SettlementRepository
) {

    @ParameterizedTest
    @AutoSource
    fun sut_updates_isSettled_of_recipient_to_true_when_is_transferred(
        price: PriceAmount,
        settlement: Settlement,
        settledRecipient1: Recipient,
        settledRecipient2: Recipient,
        notSettledRecipient: Recipient
    ) {
        //Arrange
        val sut = TransferRequestedSettlementCommandExecutor(
            settlementRepository
        )
        val arrangeSettlement = settlement.copy(
            id = ObjectId.get().toHexString(),
            status = SettlementStatus.PENDING,
            recipients = listOf(
                settledRecipient1.copy(isSettled = false),
                settledRecipient2.copy(isSettled = false),
                notSettledRecipient.copy(isSettled = false)
            )
        )
        val createdSettlement = settlementRepository.create(
            arrangeSettlement
        )
        val command = TransferRequestedSettlementCommand(
            settlementId = createdSettlement.id!!,
            userId = notSettledRecipient.userId,
            price = price
        )

        //Act
        sut.execute(command)

        //Assert
        val actual = settlementRepository.getList(
            recipientId = notSettledRecipient.userId
        ).firstOrNull()
        assertThat(actual).isNotNull
        assertThat(
            actual!!.recipients.first { it.userId == notSettledRecipient.userId }
                .isSettled
        )
            .isTrue
    }

    @ParameterizedTest
    @AutoSource
    fun sut_updates_status_of_settlement_to_SETTLED_when_is_transferred(
        price: PriceAmount,
        settlement: Settlement,
        settledRecipient1: Recipient,
        settledRecipient2: Recipient,
        notSettledRecipient: Recipient
    ) {
        //Arrange
        val sut = TransferRequestedSettlementCommandExecutor(
            settlementRepository
        )
        val arrangeSettlement = settlement.copy(
            id = ObjectId.get().toHexString(),
            status = SettlementStatus.PENDING,
            recipients = listOf(
                settledRecipient1.copy(isSettled = true),
                settledRecipient2.copy(isSettled = true),
                notSettledRecipient.copy(isSettled = false)
            )
        )
        val createdSettlement = settlementRepository.create(
            arrangeSettlement
        )
        val command = TransferRequestedSettlementCommand(
            settlementId = createdSettlement.id!!,
            userId = notSettledRecipient.userId,
            price = price
        )

        //Act
        sut.execute(command)

        //Assert
        val actual = settlementRepository.getList(
            requesterId = settlement.requesterId
        ).firstOrNull()
        assertThat(actual).isNotNull
        assertThat(
            actual!!.status
        )
            .isEqualTo(SettlementStatus.SETTLED)
    }

    @ParameterizedTest
    @AutoSource
    fun sut_adds_transaction_once_it_is_settled(
        price: PriceAmount,
        settlement: Settlement,
        settledRecipient1: Recipient,
        settledRecipient2: Recipient,
        notSettledRecipient: Recipient
    ) {
        //Arrange
        val sut = TransferRequestedSettlementCommandExecutor(
            settlementRepository
        )
        val arrangeSettlement = settlement.copy(
            id = ObjectId.get().toHexString(),
            status = SettlementStatus.PENDING,
            recipients = listOf(
                settledRecipient1.copy(isSettled = true),
                settledRecipient2.copy(isSettled = true),
                notSettledRecipient.copy(isSettled = false)
            ),
            transactions = listOf()
        )
        val createdSettlement = settlementRepository.create(
            arrangeSettlement
        )
        val command = TransferRequestedSettlementCommand(
            settlementId = createdSettlement.id!!,
            userId = notSettledRecipient.userId,
            price = price
        )

        //Act
        sut.execute(command)

        //Assert
        val actual = settlementRepository.getList(
            requesterId = settlement.requesterId
        ).firstOrNull()
        assertThat(actual).isNotNull
        assertThat(actual!!.transactions).hasSize(1)
        val firstActual = actual!!.transactions.first()
        assertThat(firstActual.id).isNotNull
        assertThat(firstActual.price).isEqualTo(command.price)
        assertThat(firstActual.type).isEqualTo(TransactionType.SETTLEMENT)
        assertThat(firstActual.createdDateTimeUtc)
            .isCloseTo(OffsetDateTime.now(ZoneOffset.UTC), Assertions.within(1, ChronoUnit.SECONDS))
    }

    @ParameterizedTest
    @AutoSource
    fun sut_throws_InvalidRequestException_when_settlementId_of_command_does_not_exist(
        price: PriceAmount,
        settlementId: String,
        userId: String
    ) {
        //Arrange
        val sut = TransferRequestedSettlementCommandExecutor(
            settlementRepository
        )
        val command = TransferRequestedSettlementCommand(
            settlementId = settlementId,
            userId = userId,
            price = price
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
            .isEqualTo("settlementId")
        assertThat(actual!!.errorProperties[0].errorReason)
            .isEqualTo(ErrorReason.NotFound)
    }
}
