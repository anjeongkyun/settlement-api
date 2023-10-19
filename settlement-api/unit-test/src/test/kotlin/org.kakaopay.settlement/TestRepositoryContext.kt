import com.mongodb.ConnectionString
import org.kakaopay.settlement.MongoDbConfiguration
import org.kakaopay.settlement.repositories.SettlementRepository
import org.kakaopay.settlement.repositories.SettlementRepositoryImpl
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

@TestConfiguration
open class TestRepositoryContext {
    @Bean
    open fun mongoCustomConversions(): MongoCustomConversions? {
        return MongoCustomConversions(
            listOf(
                MongoOffsetDateTimeWriter(),
                MongoOffsetDateTimeReader()
            )
        )
    }

    class MongoOffsetDateTimeReader : Converter<Date, OffsetDateTime> {
        override fun convert(date: Date): OffsetDateTime {
            return OffsetDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"))
        }
    }

    class MongoOffsetDateTimeWriter : Converter<OffsetDateTime, Date> {
        override fun convert(offsetDateTime: OffsetDateTime): Date {
            return Date.from(offsetDateTime.toInstant())
        }
    }

    @Bean
    open fun mongoDatabaseFactory(): MongoDatabaseFactory {
        return SimpleMongoClientDatabaseFactory(ConnectionString("mongodb://localhost:3333/test"))
    }

    @Bean
    open fun mongodbConfiguration(
        mongoDatabaseFactory: MongoDatabaseFactory,
        mongoCustomConversions: MongoCustomConversions
    ): MongoDbConfiguration {
        return MongoDbConfiguration(mongoDatabaseFactory, mongoCustomConversions)
    }

    @Bean
    open fun settlementRepository(
        mongodbConfiguration: MongoDbConfiguration
    ): SettlementRepository {
        return SettlementRepositoryImpl(mongodbConfiguration)
    }
}
