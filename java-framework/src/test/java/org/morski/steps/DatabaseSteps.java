package org.morski.steps;

import io.qameta.allure.Step;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseSteps {

    private final Connection connection;

    public DatabaseSteps(Connection connection) {
        this.connection = connection;
    }

    @Step("Verify account id {accountId} has valid data in DB")
    public void verifyAccount(String accountId, String expectedCustomerId,
                              int expectedBalance, String expectedCurrency) throws Exception {
        String sql = "SELECT customer_id, balance, currency FROM accounts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(accountId));
            try (ResultSet rs = stmt.
                    executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString("customer_id")).isEqualTo(expectedCustomerId);
                assertThat(rs.getDouble("balance")).isEqualTo(expectedBalance);
                assertThat(rs.getString("currency")).isEqualTo(expectedCurrency);
            }
        }
    }
}
