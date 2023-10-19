package org.kakaopay.settlement.mappers

import org.kakaopay.settlement.entities.Settlement
import org.kakaopay.settlement.models.SettlementDataModel

object SettlementDataMapper {
    fun toDocument(entity: Settlement): SettlementDataModel {
        return SettlementDataModel(
            entity.id,
            entity.price,
            entity.status,
            entity.requesterId,
            entity.recipients,
            entity.transactions
        )
    }

    fun toEntity(dataModel: SettlementDataModel): Settlement {
        return Settlement(
            dataModel.id,
            dataModel.price,
            dataModel.status,
            dataModel.requesterId,
            dataModel.recipients,
            dataModel.transactions
        )
    }
}
