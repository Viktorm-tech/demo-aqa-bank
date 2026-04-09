package org.morski.base;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseDbTest {

    protected static PostgreSQLContainer<?> postgres;
    protected static Connection connection;

    @BeforeAll
    @SuppressWarnings("resource")
    public static void setUp() throws SQLException {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        postgres.start();

        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();

        connection = DriverManager.getConnection(jdbcUrl, username, password);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS accounts (
                        id SERIAL PRIMARY KEY,
                        customer_id INTEGER NOT NULL,
                        balance NUMERIC(15,2) NOT NULL,
                        currency VARCHAR(3) NOT NULL
                    )
                    """);
        }
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        if (connection != null) connection.close();
        if (postgres != null) postgres.stop();
    }
}
