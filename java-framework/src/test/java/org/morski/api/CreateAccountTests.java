package org.morski.api;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.morski.base.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.morski.db.DatabaseClient;
import org.morski.kafka.KafkaConsumerClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Epic("API Tests")
@Story("Account operations")
public class CreateAccountTests extends BaseApiTest {

    @Test
    @DisplayName("Create new account")
    @Description("Positive: POST /api/accounts creates account and returns 201")
    public void createAccountTest() throws Exception {
        String requestBody = """
                {
                    "customerId": 10,
                    "initialBalance": 500,
                    "currency": "EUR"
                }
                """;

        var responseBody = given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/accounts")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("balance", equalTo(500))
                .body("currency", equalTo("EUR"))
                .body("status", equalTo("ACTIVE"));

        var accountId = responseBody.extract().path("id");

        String selectSql = "SELECT customer_id, balance, currency FROM accounts WHERE customer_id = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(selectSql)) {
            stmt.setInt(1, 10);
            try (ResultSet rs = stmt.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt("customer_id")).isEqualTo(10);
                assertThat(rs.getDouble("balance")).isEqualTo(500.00);
                assertThat(rs.getString("currency")).isEqualTo("EUR");
            }
        }

        ConsumerRecord<String, String> record = kafkaConsumer.poll(Duration.ofSeconds(10));
        assertThat(record).isNotNull();
        assertThat(record.topic()).isEqualTo("account-events");
        String event = record.value();
        assertThat(event).contains("\"eventType\":\"ACCOUNT_CREATED\"");
        assertThat(event).contains("\"accountId\":" + accountId);
    }
}
