package org.kakaopay.settlement.usecases.quries

import autoparams.AutoSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.kakaopay.settlement.TestRepositoryContext
import org.kakaopay.settlement.entities.Settlement
import org.kakaopay.settlement.queries.GetSettlementForRequesterQuery
import org.kakaopay.settlement.repositories.SettlementRepository
import org.kakaopay.settlement.usecases.queries.GetSettlementsForRequesterQueryProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ContextConfiguration
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@DataMongoTest
@ContextConfiguration(classes = [TestRepositoryContext::class])
class SpecsForGetSettlementsForRequesterQueryProcessor(
    @Autowired val settlementRepository: SettlementRepository
) {

    @ParameterizedTest
    @AutoSource
    fun sut_returns_settlements_with_requesterId_of_query_correctly(
        settlement: Settlement,
        @Min(1) @Max(10) size: Int
    ) {
        //Arrange
        arrangeSettlements(settlement, size)
        val sut = GetSettlementsForRequesterQueryProcessor(
            settlementRepository
        )
        val query = GetSettlementForRequesterQuery(
            settlement.requesterId
        )

        //Act
        val actual = sut.process(query)

        //Assert
        assertThat(actual).isNotNull
        assertThat(actual.settlements).hasSize(size)
        val firstSettlement = actual.settlements.first()
        assertThat(firstSettlement.price).isNotNull
        assertThat(firstSettlement.price).isEqualTo(settlement.price)
        assertThat(firstSettlement.recipients).isEqualTo(settlement.recipients)
        assertThat(firstSettlement.status).isEqualTo(settlement.status)
    }

    private fun arrangeSettlements(
        settlement: Settlement,
        size: Int
    ) {
        (0 until size).forEach { _ ->
            settlementRepository.create(
                settlement.copy(id = null)
            )
        }
    }
}
