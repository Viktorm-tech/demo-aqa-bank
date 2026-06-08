package org.morski.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.morski.base.BaseTest;
import org.morski.dto.AccountResponse;

import static java.util.UUID.randomUUID;
import static org.morski.steps.AccountSteps.createDefaultAccount;
import static org.morski.validators.ApiValidators.validateAccountResponse;

@Epic("Integration Tests")
@Story("Account operations")
public class GetAccountDetailsTests extends BaseTest {

    @Test
    @DisplayName("Get account details")
    @Description("Positive: GET /api/accounts/{id} returns account data")
    public void getAccountTest() {

        var account = createDefaultAccount();
        databaseSteps.createAccount(account);

        var response = apiSteps.getAccountById(account.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(AccountResponse.class);

        validateAccountResponse(response, account);
    }


    @Test
    @DisplayName("Get account not exist")
    @Description("Negative: GET /api/accounts/{id} returns 404 if account not exist")
    public void getAccountNotExistTest() {

        apiSteps.getAccountById(randomUUID())
                .then()
                .statusCode(404);
    }
}
