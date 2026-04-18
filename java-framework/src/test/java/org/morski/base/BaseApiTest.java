package org.morski.base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.morski.db.DatabaseClient;
import org.morski.kafka.KafkaConsumerClient;

import java.sql.Connection;

public abstract class BaseApiTest {

    protected static RequestSpecification requestSpec;
    protected static Connection dbConnection;
    protected static KafkaConsumerClient kafkaConsumer;
    private static DatabaseClient dbClient;

    @BeforeAll
    public static void setup() throws Exception{
        RestAssured.baseURI = "http://localhost:8080";

        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        dbClient = new DatabaseClient();
        dbConnection = dbClient.getConnection();
        kafkaConsumer = new KafkaConsumerClient("account-events");
    }

    @AfterAll
    public static void tearDown() throws Exception {
        kafkaConsumer.close();
        dbClient.close();
    }
}
