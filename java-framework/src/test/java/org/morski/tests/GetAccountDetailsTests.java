package org.morski.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Integration Tests")
@Story("Account operations")
public class GetAccountDetailsTests {

    @Test
    @DisplayName("Get account details")
    @Description("Positive: GET /api/accounts/{id} returns account data")
    public void createAccountTest() throws Exception {

    }
}
