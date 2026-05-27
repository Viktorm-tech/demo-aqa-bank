package org.morski.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.morski.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Integration Tests")
@Story("Account operations")
public class CreateAccountTests extends BaseTest {

    @Test
    @DisplayName("Create new account")
    @Description("Positive: POST /api/accounts creates account and returns 201")
    public void createAccountTest() throws Exception {
        var customerId = "10";
        var initialBalance = new BigDecimal("500.00");
        var currency = "EUR";

        var response = accountApiSteps.createAccount(customerId, initialBalance, currency);

        response.then()
                .body("balance", equalTo(initialBalance))
                .body("currency", equalTo(currency))
                .body("customerId", equalTo(customerId))
                .body("status", equalTo("ACTIVE"));

        String accountId = response.path("id");

        databaseSteps.verifyAccount(accountId, customerId, initialBalance, currency);

        var record = kafkaSteps.waitForEvent(accountId, "account-events", Duration.ofSeconds(10));

        assertThat(record).isNotNull();
        String event = record.value();
        var mapper = new ObjectMapper();
        var jsonNode = mapper.readTree(event);
        assertEquals("ACCOUNT_CREATED", jsonNode.get("eventType").asText());
        assertEquals(accountId, jsonNode.get("accountId").asText());
        assertEquals("10", jsonNode.get("data").get("customerId").asText());
    }
}
