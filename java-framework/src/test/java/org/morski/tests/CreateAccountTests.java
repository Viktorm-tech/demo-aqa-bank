package org.morski.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.morski.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("API Tests")
@Story("Account operations")
public class CreateAccountTests extends BaseTest {

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

        String accountId = responseBody.extract().path("id");

        String selectSql = "SELECT customer_id, balance, currency FROM accounts WHERE customer_id = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(selectSql)) {
            stmt.setString(1, "10");
            try (ResultSet rs = stmt.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt("customer_id")).isEqualTo(10);
                assertThat(rs.getDouble("balance")).isEqualTo(500.00);
                assertThat(rs.getString("currency")).isEqualTo("EUR");
            }
        }

        ConsumerRecord<String, String> record = kafkaClient.waitForRecordByKey(
                accountId,
                "account-events",
                Duration.ofSeconds(10)
        );

        assertThat(record).isNotNull();
        String event = record.value();
        var mapper = new ObjectMapper();
        var jsonNode = mapper.readTree(event);
        assertEquals("ACCOUNT_CREATED", jsonNode.get("eventType").asText());
        assertEquals(accountId, jsonNode.get("accountId").asText());
        assertEquals("10", jsonNode.get("data").get("customerId").asText());
    }
}
