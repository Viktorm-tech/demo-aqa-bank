package org.morski.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.morski.config.TestConfig;
import org.morski.db.DatabaseClient;
import org.morski.kafka.KafkaClient;
import org.morski.steps.ApiSteps;
import org.morski.steps.DatabaseSteps;
import org.morski.steps.KafkaSteps;

import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public abstract class BaseTest {

    private static DatabaseClient dbClient;
    private KafkaConsumer<String, String> kafkaConsumer;

    protected static ApiSteps apiSteps;
    protected static DatabaseSteps databaseSteps;
    protected KafkaSteps kafkaSteps;

    @BeforeAll
    public static void setup() throws Exception{
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.config = RestAssuredConfig.config()
                .jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));

        var requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        apiSteps = new ApiSteps(requestSpec);

        dbClient = new DatabaseClient();
        databaseSteps = new DatabaseSteps(dbClient);
    }

    @BeforeEach
    void setUpKafka() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, TestConfig.getKafkaBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + UUID.randomUUID());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(Collections.singletonList("account-events"));
        KafkaClient kafkaClient = new KafkaClient(kafkaConsumer);
        kafkaSteps = new KafkaSteps(kafkaClient);
    }

    @AfterEach
    void tearDownKafka() {
        if (kafkaConsumer != null) {
            kafkaConsumer.close();
        }
    }

    @AfterAll
    public static void tearDown() throws Exception {
        dbClient.close();
    }
}
