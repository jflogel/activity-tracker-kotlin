package activity.tracker.configuration

import com.mongodb.MongoClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate


@Configuration
open class MongoConfiguration {

    @Value("\${database.name}")
    lateinit var databaseName: String

    @Value("\${mongo.host}")
    lateinit var mongoHost: String

    @Bean
    @Throws(Exception::class)
    open fun mongoTemplate(): MongoTemplate {
        val mongoClient = MongoClient(mongoHost)
        return MongoTemplate(mongoClient, databaseName)
    }
}