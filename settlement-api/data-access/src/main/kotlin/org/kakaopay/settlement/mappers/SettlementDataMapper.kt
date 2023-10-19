package org.kakaopay.settlement.mappers

import org.bson.types.ObjectId
import org.kakaopay.settlement.entities.Settlement
import org.kakaopay.settlement.models.SettlementDataModel

object SettlementDataMapper {
    fun toEntity(dataModel: SettlementDataModel): Settlement {
        return Settlement(
            dataModel.id?.toHexString(),
            dataModel.price,
            dataModel.status,
            dataModel.requesterId,
            dataModel.recipients,
            dataModel.transactions
        )
    }
    
    fun toDocument(entity: Settlement): SettlementDataModel {
        return SettlementDataModel(
            entity.id?.let { ObjectId(it) },
            entity.price,
            entity.status,
            entity.requesterId,
            entity.recipients,
            entity.transactions
        )
    }
}
