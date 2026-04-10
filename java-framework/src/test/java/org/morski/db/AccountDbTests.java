package org.morski.db;

import org.morski.base.BaseDbTest;
import org.morski.db.queries.AccountQueries;
import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountDbTests extends BaseDbTest {

    @Test
    @DisplayName("Check existence of an account in DB")
    @Description("Insert a test account and check its availability")
    public void accountShouldExistInDatabase() throws Exception {
        Long accountId = AccountQueries.insertTestAccount(connection, 1, 100.0, "USD");
        boolean exists = AccountQueries.accountExists(connection, accountId);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Account balance must be correct")
    @Description("Check that the account balance in the database matches the expected value")
    public void accountBalanceShouldBeCorrect() throws Exception {
        Long accountId = AccountQueries.insertTestAccount(connection, 2, 250.0, "USD");
        Double balance = AccountQueries.getAccountBalance(connection, accountId);
        assertThat(balance).isEqualTo(250.0);
    }
}
