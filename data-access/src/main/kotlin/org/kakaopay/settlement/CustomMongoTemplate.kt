package org.kakaopay.settlement

import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions

class CustomMongoTemplate(
    mongoDatabaseFactory: MongoDatabaseFactory,
    conversions: MongoCustomConversions
) : MongoTemplate(mongoDatabaseFactory) {

    init {
        val converter = this.getConverter() as MappingMongoConverter
        converter.customConversions = conversions
        converter.typeMapper = DefaultMongoTypeMapper(null)
        converter.afterPropertiesSet()
    }

    override fun getCollectionName(entityClass: Class<*>): String {
        return super.getCollectionName(entityClass)
    }
}
