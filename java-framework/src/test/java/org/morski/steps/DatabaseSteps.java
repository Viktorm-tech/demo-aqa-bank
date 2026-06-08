package org.morski.steps;

import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import org.morski.constants.AccountStatus;
import org.morski.db.DatabaseClient;
import org.morski.dto.Account;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.morski.utils.Utils.toLocalDateTime;
import static org.morski.utils.Utils.truncateToSeconds;

public class DatabaseSteps {

    private final DatabaseClient dbClient;

    public DatabaseSteps(DatabaseClient dbClient) {
        this.dbClient = dbClient;
    }

    private Map<String, Object> getAccountFromDB(UUID accountId) {
        var sql = "SELECT * FROM accounts WHERE id = ?";
        var rows = dbClient.executeQuery(sql, accountId);
        assertThat(rows).as("Amount of records with id = %s", accountId).hasSize(1);
        return rows.getFirst();
    }

    @Step("Create account in DB")
    public void createAccount(Account account) {
        var sql ="INSERT INTO accounts (id, customer_id, balance, currency, status, created_at, updated_at)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";
        var updateCount = dbClient.executeUpdate(
                sql,
                account.getId(),
                account.getCustomerId(),
                account.getBalance(),
                account.getCurrency().name(),
                account.getStatus().name(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
        assertThat(updateCount).as("Updated DB rows").isEqualTo(1);
    }

    @Step("Verify account id {accountId} has valid data in DB")
    public void verifyAccount(UUID accountId, Account account) {
        var row = getAccountFromDB(accountId);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(row.get("customer_id")).as("customer_id").isEqualTo(account.getCustomerId());
            softly.assertThat(row.get("balance")).as("balance").isEqualTo(account.getBalance());
            softly.assertThat(row.get("currency")).as("currency").isEqualTo(account.getCurrency().name());
            softly.assertThat(row.get("status")).as("status").isEqualTo(AccountStatus.ACTIVE.name());
        });
    }

    @Step("Verify balance increased by {amount}")
    public void verifyBalanceIncreased(Account account, BigDecimal amount, Instant updatedAt) {
        var row = getAccountFromDB(account.getId());

        LocalDateTime dbCreatedAt = toLocalDateTime(row.get("created_at"));
        LocalDateTime dbUpdatedAt = toLocalDateTime(row.get("updated_at"));

        LocalDateTime expectedCreatedAt = account.getCreatedAt();
        LocalDateTime expectedUpdatedAt = LocalDateTime.ofInstant(updatedAt, ZoneOffset.UTC);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(row.get("customer_id")).as("customer_id").isEqualTo(account.getCustomerId());
            softly.assertThat(row.get("balance")).as("balance").isEqualTo(account.getBalance().add(amount));
            softly.assertThat(row.get("currency")).as("currency").isEqualTo(account.getCurrency().name());
            softly.assertThat(row.get("status")).as("status").isEqualTo(account.getStatus().name());
            softly.assertThat(truncateToSeconds(dbCreatedAt))
                    .as("created_at")
                    .isEqualTo(truncateToSeconds(expectedCreatedAt));
            softly.assertThat(truncateToSeconds(dbUpdatedAt))
                    .as("updated_at")
                    .isEqualTo(truncateToSeconds(expectedUpdatedAt));
        });
    }
}
