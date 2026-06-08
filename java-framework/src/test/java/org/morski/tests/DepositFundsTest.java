package org.morski.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.morski.base.BaseTest;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.morski.steps.AccountSteps.createDefaultAccount;

@Epic("Integration Tests")
@Story("Deposit operations")
public class DepositFundsTest extends BaseTest {

    @Test
    @DisplayName("Deposit funds")
    @Description("Positive: POST /api/accounts/{id}/deposit returns 200 OK amount added to balance")
    public void depositFundsTest() {

        var account = createDefaultAccount();
        databaseSteps.createAccount(account);
        var amount = BigDecimal.valueOf(500, 2);

        var response = apiSteps.deposit(account.getId().toString(), amount);
        response.then().statusCode(200);
        var headerDate = response.getHeader("Date");
        var updatedAt = ZonedDateTime.parse(headerDate, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();


        databaseSteps.verifyBalanceIncreased(account, amount, updatedAt);
    }
}
