package com.quodex._miles.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;

import java.util.List;

@Configuration
@EnableConfigurationProperties(MongoDbProperties.class)
@ConditionalOnProperty(prefix = "app.mongodb", name = "enabled", havingValue = "true")
public class MongoDbConfig {

    @Bean
    public MongoClient mongoClient(MongoDbProperties properties) {
        if (StringUtils.hasText(properties.getUri())) {
            return MongoClients.create(new ConnectionString(properties.getUri()));
        }

        if (!StringUtils.hasText(properties.getDatabase())) {
            throw new IllegalStateException(
                    "app.mongodb.database must be configured when app.mongodb.uri is not provided");
        }

        var settingsBuilder = com.mongodb.MongoClientSettings.builder()
                .applyToClusterSettings(settings ->
                        settings.hosts(List.of(new ServerAddress(properties.getHost(), properties.getPort()))));

        if (StringUtils.hasText(properties.getUsername()) && StringUtils.hasText(properties.getPassword())) {
            settingsBuilder.credential(MongoCredential.createCredential(
                    properties.getUsername(),
                    properties.getDatabase(),
                    properties.getPassword().toCharArray()
            ));
        }

        return MongoClients.create(settingsBuilder.build());
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient, MongoDbProperties properties) {
        String databaseName = StringUtils.hasText(properties.getUri())
                ? new ConnectionString(properties.getUri()).getDatabase()
                : properties.getDatabase();

        if (!StringUtils.hasText(databaseName)) {
            throw new IllegalStateException("Mongo database name could not be resolved from configuration");
        }

        return new MongoTemplate(mongoClient, databaseName);
    }
}
