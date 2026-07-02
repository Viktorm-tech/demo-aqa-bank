package org.morski.tests;

import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.morski.base.BaseTest;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.morski.steps.AccountSteps.createAccountWithBalance;

public class WithdrawFundsTest extends BaseTest {
    @Test
    @DisplayName("Withdraw funds")
    @Description("Positive: POST /api/accounts/{id}/withdraw returns 200 OK amount debited from balance")
    public void withdrawFundsTest() {

        var initialBalance = BigDecimal.valueOf(500, 2);
        var account = createAccountWithBalance(initialBalance);
        databaseSteps.createAccount(account);

        var withdrawAmount = BigDecimal.valueOf(200, 2);
        var response = apiSteps.withdraw(account.getId().toString(), withdrawAmount);
        response.then().statusCode(200);

        var headerDate = response.getHeader("Date");
        var updatedAt = ZonedDateTime.parse(headerDate, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
        var expectedBalance = account.getBalance().subtract(withdrawAmount);
        databaseSteps.verifyBalanceChanged(account, expectedBalance, updatedAt);
    }
}
