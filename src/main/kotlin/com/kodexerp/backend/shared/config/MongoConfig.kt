package com.kodexerp.backend.shared.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import org.bson.UuidRepresentation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["com.kodexerp.backend.auth", "com.kodexerp.backend.admin", "com.kodexerp.backend.subscription", "com.kodexerp.backend.customers"])
class MongoConfig : AbstractMongoClientConfiguration() {

    override fun getDatabaseName(): String {
        return "kodex"
    }

    @Bean
    override fun mongoClientSettings(): MongoClientSettings {
        val connectionString = ConnectionString("mongodb://admin:M0ng0%40%232026%21xP@74.0.5.52:27017/kodex?authSource=admin")
        return MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .build()
    }
}
