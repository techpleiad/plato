package org.techpleiad.plato.deploy;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ConditionalOnProperty(prefix = "plato.mongo.properties", name = "database")
@EnableMongoRepositories(basePackages = {"org.techpleiad.plato.*"})
@AutoConfigureBefore(MongoAutoConfiguration.class)
@EnableTransactionManagement
@ComponentScan(basePackages = {"org.techpleiad.plato.*"})
@EnableAsync
@SpringBootApplication
@Slf4j
public class MongoConfiguration {

    @Value("${plato.mongo.properties.uri:}")
    private String connectionURI;

    @Value("${plato.mongo.properties.database:}")
    private String database;

    @Value("${plato.mongo.properties.sslEnabled:}")
    private Boolean sslEnabled;

    @Value("${plato.mongo.properties.sslSuffix:}")
    private String sslSuffix;

    @Value("${plato.mongo.properties.trustStorePath:}")
    private String trustStorePath;

    @Value("${plato.mongo.properties.trustStorePass:}")
    private String trustStorePass;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    @Bean
    @Primary
    public MongoClient mongoClient() {
        final MongoClient client;
        if (!connectionURI.isEmpty()) {
            log.info("Creating mongoClient");
            client = createClient(connectionURI);
        } else {
            log.error("Unable to Create MongoClient because config properties is required [uri]");
            throw new RuntimeException("config properties is required [uri]");
        }
        return client;
    }

    /**
     * Private Helper method to create custom MongoClient
     *
     * @param connectionString
     * @return MongoClient
     */
    private MongoClient createClient(String connectionString) {
        final MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder();
        if (sslEnabled) {
            connectionString = connectionString + sslSuffix;
            settingsBuilder.applyToSslSettings(builder -> {
                builder.enabled(true);
                builder.context(securityConfiguration.getSslContext(trustStorePath, trustStorePass));
            });
        }
        settingsBuilder.applyConnectionString(new ConnectionString(connectionString)).build();
        return MongoClients.create(settingsBuilder.build());
    }

    @Bean
    @Primary
    public SimpleMongoClientDatabaseFactory mongoClientDatabaseFactory(@Autowired final MongoClient mongoClient) {
        log.info("Creating CustomMongoDbFactory using CustomMongoClient");
        return new SimpleMongoClientDatabaseFactory(mongoClient, database);
    }

    @Bean
    public MongoTemplate mongoTemplate(final MongoDatabaseFactory mongoDatabaseFactory) {
        log.info("Created Custom MongoTemplate using MongoDatabaseFactory & DTMongoCollectionPropertiesSecured");
        return new MongoTemplate(mongoDatabaseFactory);
    }
}

