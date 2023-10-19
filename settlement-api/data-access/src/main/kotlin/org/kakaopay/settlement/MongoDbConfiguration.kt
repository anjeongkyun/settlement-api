package org.kakaopay.settlement

import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.MongoCustomConversions

class MongoDbConfiguration(
    private val mongoDatabaseFactory: MongoDatabaseFactory,
    private val conversions: MongoCustomConversions
) {

    fun getMongoTemplate(): MongoTemplate {
        return CustomMongoTemplate(mongoDatabaseFactory, conversions)
    }
}
