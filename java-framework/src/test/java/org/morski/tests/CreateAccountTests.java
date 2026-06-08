package org.morski.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.morski.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.morski.constants.Currency;
import org.morski.dto.Account;
import org.morski.dto.AccountResponse;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.morski.validators.ApiValidators.validateNewAccountResponse;

@Epic("Integration Tests")
@Story("Account operations")
public class CreateAccountTests extends BaseTest {

    @Test
    @DisplayName("Create new account")
    @Description("Positive: POST /api/accounts creates account and returns 201")
    public void createAccountTest() throws Exception {
        var account = Account.builder()
                .customerId("acc123")
                .balance(BigDecimal.valueOf(500, 2))
                .currency(Currency.EUR)
                .build();

        var response = apiSteps.createAccount(account)
                .then()
                .statusCode(201)
                .extract()
                .as(AccountResponse.class);


        validateNewAccountResponse(response, account);

        var accountId = response.getId();

        databaseSteps.verifyAccount(accountId, account);

        var record = kafkaSteps.waitForEvent(accountId.toString(), "account-events", Duration.ofSeconds(10));

        assertThat(record).isNotNull();
        String event = record.value();
        var mapper = new ObjectMapper();
        var jsonNode = mapper.readTree(event);
        assertEquals("ACCOUNT_CREATED", jsonNode.get("eventType").asText());
        assertEquals(accountId.toString(), jsonNode.get("accountId").asText());
        assertEquals(String.valueOf(account.getCustomerId()), jsonNode.get("data").get("customerId").asText());
    }
}
