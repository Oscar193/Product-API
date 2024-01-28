package com.textplus.product;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import jakarta.validation.constraints.NotNull;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.textplus.product.UserConfiguration.PASSWORD_1;
import static com.textplus.product.UserConfiguration.USER_NAME_1;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@ContextConfiguration(initializers = ITBase.Initializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class ITBase {

    private static final MongoDBContainer MONGO_DB_CONTAINER;
    private static final MongoClient MONGO_CLIENT;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static final String TEST_DB = "testDB";

    protected abstract void populateDatabase() throws Exception;


    static {
        MONGO_DB_CONTAINER = new MongoDBContainer("mongo:4.2");
        MONGO_DB_CONTAINER.start();

        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromRegistries(pojoCodecRegistry));
        ConnectionString connectionString = new ConnectionString(MONGO_DB_CONTAINER.getReplicaSetUrl());
        MongoClientSettings clientSettings = MongoClientSettings.builder().applyConnectionString(connectionString).codecRegistry(codecRegistry).build();
        MONGO_CLIENT = MongoClients.create(clientSettings);
    }

    @BeforeEach
    public final void setup() throws Exception {
        populateDatabase();
    }

    @AfterEach
    public void clearMongoDB() {
        MONGO_CLIENT.getDatabase(TEST_DB).drop();
    }

    protected HttpEntity<Void> getEntity() {
        return getEntity(null);
    }

    protected <T> HttpEntity<T> getEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(USER_NAME_1, PASSWORD_1);
        headers.set(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.set(ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(body, headers);
    }

    protected <T> MongoCollection<T> getCollection(String collectionName, Class<T> dbEntity) {
        return MONGO_CLIENT.getDatabase(TEST_DB)
                .getCollection(collectionName, dbEntity);
    }

    protected TestRestTemplate getTestRestTemplate() {
        return testRestTemplate;
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.data.mongodb.uri=" + MONGO_DB_CONTAINER.getReplicaSetUrl(TEST_DB)
            );

            values.applyTo(configurableApplicationContext);
        }
    }
}
