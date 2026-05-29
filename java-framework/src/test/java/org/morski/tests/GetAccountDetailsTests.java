package org.morski.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.morski.base.BaseTest;

import static org.morski.steps.AccountSteps.createDefaultAccount;
import static org.morski.validators.ApiValidators.validateAccountResponse;

@Epic("Integration Tests")
@Story("Account operations")
public class GetAccountDetailsTests extends BaseTest {

    @Test
    @DisplayName("Get account details")
    @Description("Positive: GET /api/accounts/{id} returns account data")
    public void GetAccountTest() throws Exception {

        var account = createDefaultAccount();
        databaseSteps.createAccount(account);

        var response = accountApiSteps.getAccountById(account.getId());

        validateAccountResponse(response, account);
    }
}
