package org.morski.config;

import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream("test.properties")) {
            if (input != null) {
                props.load(input);
            } else {
                props.setProperty("db.url", System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/bankingdb"));
                props.setProperty("db.user", System.getenv().getOrDefault("DB_USER", "bankinguser"));
                props.setProperty("db.password", System.getenv().getOrDefault("DB_PASSWORD", "bankingpass"));
                props.setProperty("kafka.bootstrap.servers", System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"));
                props.setProperty("kafka.topic.account.events", System.getenv().getOrDefault("KAFKA_TOPIC_ACCOUNT_EVENTS", "account-events"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test configuration", e);
        }
    }

    public static String getDbUrl() { return props.getProperty("db.url"); }
    public static String getDbUser() { return props.getProperty("db.user"); }
    public static String getDbPassword() { return props.getProperty("db.password"); }
    public static String getKafkaBootstrapServers() { return props.getProperty("kafka.bootstrap.servers"); }
    public static String getKafkaAccountEventsTopic() { return props.getProperty("kafka.topic.account.events"); }
}
