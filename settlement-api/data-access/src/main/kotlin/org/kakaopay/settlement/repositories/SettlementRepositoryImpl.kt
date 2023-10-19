package org.kakaopay.settlement.repositories

import org.kakaopay.settlement.MongoDbConfiguration
import org.kakaopay.settlement.entities.Settlement
import org.kakaopay.settlement.mappers.SettlementDataMapper
import org.kakaopay.settlement.models.SettlementDataModel
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class SettlementRepositoryImpl(
    private val database: MongoDbConfiguration
) : SettlementRepository {
    private val entityType: Class<SettlementDataModel> = SettlementDataModel::class.java

    //TODO: Index 고민
    private fun getCollection(): MongoTemplate {
        return database.getMongoTemplate()
    }

    override fun getList(
        requesterId: String?,
        recipientId: String?
    ): List<Settlement> {
        val query = Query()
        requesterId?.let { query.addCriteria(Criteria.where("requesterId").`is`(it)) }
        recipientId?.let { query.addCriteria(Criteria.where("recipientId").`is`(it)) }
        return getCollection()
            .find(query, entityType)
            .map { SettlementDataMapper.toEntity(it) }
    }

    override fun create(settlement: Settlement): Settlement {
        return SettlementDataMapper
            .toEntity(
                getCollection()
                    .save(SettlementDataMapper.toDocument(settlement))
            )
    }
}
