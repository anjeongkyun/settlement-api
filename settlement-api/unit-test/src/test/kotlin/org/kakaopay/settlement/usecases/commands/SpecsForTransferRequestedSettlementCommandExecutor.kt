package org.kakaopay.settlement.usecases.commands

import autoparams.AutoSource
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.params.ParameterizedTest
import org.kakaopay.settlement.*
import org.kakaopay.settlement.commands.TransferRequestedSettlementCommand
import org.kakaopay.settlement.entities.Settlement
import org.kakaopay.settlement.repositories.SettlementRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ContextConfiguration
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

//TODO: Test 명 변경하기
@DataMongoTest
@ContextConfiguration(classes = [TestRepositoryContext::class])
class SpecsForTransferRequestedSettlementCommandExecutor(
    @Autowired val settlementRepository: SettlementRepository
) {

    @ParameterizedTest
    @AutoSource
    fun sut는_recipient의_isSettled의_값을_true로_변경한다(
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
    fun sut는_수신자들의_정산이_완료되면_settlement_status를_SETTLED로_변경한다(
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
    fun sut는_transactions에_송금한_트랜잭션을_추가한다(
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
        val firstActual = actual!!.transactions.first()
        assertThat(firstActual.id).isNotNull
        assertThat(firstActual.price).isEqualTo(command.price)
        assertThat(firstActual.type).isEqualTo(TransactionType.SETTLEMENT)
        assertThat(firstActual.createdDateTimeUtc)
            .isCloseTo(OffsetDateTime.now(ZoneOffset.UTC), Assertions.within(1, ChronoUnit.SECONDS))
    }
}
