package org.morski.tests;

import org.morski.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.morski.constants.Currency;
import org.morski.dto.Account;
import org.morski.dto.api.AccountResponse;

import java.math.BigDecimal;

import static org.morski.validators.ApiValidators.validateNewAccountResponse;

@Epic("Integration Tests")
@Story("Account operations")
public class CreateAccountTests extends BaseTest {

    @Test
    @DisplayName("Create new account")
    @Description("Positive: POST /api/accounts creates account and returns 201")
    public void createAccountTest() {

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

        kafkaSteps.verifyAccountCreatedEvent(accountId.toString(), account);
    }
}
