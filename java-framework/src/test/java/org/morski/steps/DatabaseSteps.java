package org.morski.steps;

import io.qameta.allure.Step;
import net.datafaker.Faker;
import org.assertj.core.api.SoftAssertions;
import org.morski.constants.AccountStatus;
import org.morski.db.DatabaseClient;
import org.morski.dto.Account;
import org.morski.dto.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.morski.constants.TransactionType.TRANSFER_SENT;
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

    private void createTransaction(Transaction transaction) {
        var sql = "INSERT INTO transactions (id, account_id, amount, type, related_account_id, description," +
                " timestamp) VALUES (?, ?, ?, ?, ?, ?, ?)";
        var updateCount = dbClient.executeUpdate(
                sql,
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getType().name(),
                transaction.getRelatedAccountId(),
                transaction.getDescription(),
                transaction.getTimestamp()
        );
        assertThat(updateCount).as("Updated DB rows").isEqualTo(1);
    }

    @Step("Create transactions for account {accountId}")
    public List<Transaction> createRandomTransactions(UUID accountId, int amount) {
        Faker faker = new Faker();
        List<Transaction> transactions = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            var transaction = Transaction.builder()
                    .id(randomUUID())
                    .accountId(accountId)
                    .amount(BigDecimal.valueOf((long)(Math.random() * 100), 2))
                    .type(TRANSFER_SENT)
                    .relatedAccountId(randomUUID())
                    .description(faker.commerce().vendor())
                    .timestamp(now().minusDays(1))
                    .build();
            createTransaction(transaction);
            transactions.add(transaction);
        }

        return transactions;
    }

    @Step("Create account in DB")
    public void createAccount(Account account) {
        var sql = "INSERT INTO accounts (id, customer_id, balance, currency, status, created_at, updated_at)" +
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

    @Step("Verify balance increased to {expectedBalance}")
    public void verifyBalanceChanged(Account account, BigDecimal expectedBalance, Instant updatedAt) {
        var row = getAccountFromDB(account.getId());

        LocalDateTime dbCreatedAt = toLocalDateTime(row.get("created_at"));
        LocalDateTime dbUpdatedAt = toLocalDateTime(row.get("updated_at"));

        LocalDateTime expectedCreatedAt = account.getCreatedAt();
        LocalDateTime expectedUpdatedAt = LocalDateTime.ofInstant(updatedAt, ZoneOffset.UTC);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(row.get("customer_id")).as("customer_id").isEqualTo(account.getCustomerId());
            softly.assertThat(row.get("balance")).as("balance").isEqualTo(expectedBalance);
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
