package activity.tracker.configuration

import com.mongodb.Mongo
import com.mongodb.MongoClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate


@Configuration
open class MongoConfiguration {

    @Value("\${database.name}")
    lateinit var databaseName: String

    @Bean
    @Throws(Exception::class)
    open fun mongo(): Mongo {
        return MongoClient("localhost")
    }

    @Bean
    @Throws(Exception::class)
    open fun mongoTemplate(): MongoTemplate {
        return MongoTemplate(mongo(), databaseName)
    }
}