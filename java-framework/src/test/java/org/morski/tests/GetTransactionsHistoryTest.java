package org.morski.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.morski.base.BaseTest;
import org.morski.dto.api.TransactionsHistoryResponse;

import static java.util.UUID.randomUUID;

@Epic("Integration Tests")
@Story("Get transactions history")
public class GetTransactionsHistoryTest extends BaseTest {

    @Test
    @DisplayName("Get transactions history")
    @Description("Positive: GET /api/accounts/{id}/transactions?page=0&size=10")
    public void getTransactionsHistoryTest() {

        var accountId = randomUUID();
        var transactions = databaseSteps.createRandomTransactions(accountId, 10);

        var response = apiSteps.getTransactions(accountId.toString(), 1, 10)
                .then()
                .statusCode(200)
                .extract()
                .as(TransactionsHistoryResponse.class);
    }
}
