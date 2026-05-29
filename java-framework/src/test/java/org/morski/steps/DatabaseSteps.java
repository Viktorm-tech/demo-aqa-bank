package org.morski.steps;

import io.qameta.allure.Step;
import org.morski.db.DatabaseClient;
import org.morski.dto.Account;

import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseSteps {

    private final DatabaseClient dbClient;

    public DatabaseSteps(DatabaseClient dbClient) {
        this.dbClient = dbClient;
    }

    @Step("Create account in DB")
    public void createAccount(Account account) throws SQLException {
        var sql ="INSERT INTO accounts (id, customer_id, balance, currency, status, created_at, updated_at)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";
        dbClient.executeUpdate(
                sql,
                account.getId(),
                account.getCustomerId(),
                account.getBalance(),
                account.getCurrency().name(),
                account.getStatus().name(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    @Step("Verify account id {accountId} has valid data in DB")
    public void verifyAccount(String accountId, Account account) throws SQLException {
        var sql = "SELECT customer_id, balance, currency FROM accounts WHERE id = ?";
        var rows = dbClient.executeQuery(sql, UUID.fromString(accountId));
        assertThat(rows).hasSize(1);
        assertThat(rows).singleElement().satisfies(row -> {
            assertThat(row.get("customer_id")).isEqualTo(account.getCustomerId());
            assertThat(row.get("balance")).isEqualTo(account.getBalance());
            assertThat(row.get("currency")).isEqualTo(account.getCurrency());
        });
    }
}
