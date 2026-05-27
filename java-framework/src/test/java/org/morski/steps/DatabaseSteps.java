package org.morski.steps;

import io.qameta.allure.Step;
import org.morski.db.DatabaseClient;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseSteps {

    private final DatabaseClient dbClient;

    public DatabaseSteps(DatabaseClient dbClient) {
        this.dbClient = dbClient;
    }

    @Step("Create account in DB")
    public void createAccount() throws SQLException {
        var sql ="INSERT INTO accounts (id, customer_id, balance, currency, status, created_at, updated_at)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";
        dbClient.executeUpdate(sql);
    }

    @Step("Verify account id {accountId} has valid data in DB")
    public void verifyAccount(String accountId, String expectedCustomerId,
                              BigDecimal expectedBalance, String expectedCurrency) throws SQLException {
        var sql = "SELECT customer_id, balance, currency FROM accounts WHERE id = ?";
        var rows = dbClient.executeQuery(sql, UUID.fromString(accountId));
        assertThat(rows).hasSize(1);
        assertThat(rows).singleElement().satisfies(row -> {
            assertThat(row.get("customer_id")).isEqualTo(expectedCustomerId);
            assertThat(row.get("balance")).isEqualTo(expectedBalance);
            assertThat(row.get("currency")).isEqualTo(expectedCurrency);
        });
    }
}
