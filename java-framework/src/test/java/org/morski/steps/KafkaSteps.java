package org.morski.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.qameta.allure.Step;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.SoftAssertions;
import org.morski.dto.Account;
import org.morski.dto.kafka.AccountCreatedEvent;
import org.morski.dto.kafka.BaseEvent;
import org.morski.dto.kafka.TransferCompletedEvent;
import org.morski.kafka.KafkaClient;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class KafkaSteps {

    private static final int BASE_TIMEOUT = 10;
    private static final String TOPIC = "account-events";
    private final KafkaClient kafkaClient;
    private final ObjectMapper objectMapper;

    public KafkaSteps(KafkaClient kafkaClient) {
        this.kafkaClient = kafkaClient;
        this.objectMapper = new ObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Step("Wait for Kafka message for key {key}")
    public BaseEvent waitForEvent(String key, String topic, Duration timeout) {
        ConsumerRecord<String, String> record = kafkaClient.waitForRecordByKey(key, topic, timeout);
        assertThat(record).as("Message with key %s not found", key).isNotNull();

        try {
            return objectMapper.readValue(record.value(), BaseEvent.class);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Failed to deserialize event", e);
        }
    }

    @Step("Verify ACCOUNT_CREATED event")
    public void verifyAccountCreatedEvent(String accountId, Account account, int timeout) {
        BaseEvent event = waitForEvent(accountId, TOPIC, Duration.ofSeconds(timeout));

        assertThat(event).isInstanceOf(AccountCreatedEvent.class);
        AccountCreatedEvent created = (AccountCreatedEvent) event;

        assertThat(created.getData()).as("Event data is missing").isNotNull();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(created.getEventType()).as("eventType").isEqualTo("ACCOUNT_CREATED");
        softly.assertThat(created.getAccountId()).as("accountId").isEqualTo(accountId);
        softly.assertThat(created.getRelatedAccountId()).as("relatedAccountId").isNull();
        softly.assertThat(created.getData().getCustomerId())
                .as("customerId")
                .isEqualTo(account.getCustomerId());
        softly.assertThat(created.getData().getInitialBalance())
                .as("initialBalance")
                .isEqualTo(account.getBalance());
        softly.assertThat(created.getData().getCurrency())
                .as("currency")
                .isEqualTo(account.getCurrency().name());
        softly.assertThat(created.getTimestamp()).as("timestamp").isNotNull();
        softly.assertAll();
    }

    public void verifyAccountCreatedEvent(String accountId, Account account) {
        verifyAccountCreatedEvent(accountId, account, BASE_TIMEOUT);
    }

    @Step("Verify TRANSFER_COMPLETED event")
    public void verifyTransferCompletedEvent(String senderId, String receiverId, BigDecimal amount, int timeout) {
        BaseEvent event = waitForEvent(senderId, TOPIC, Duration.ofSeconds(timeout));

        assertThat(event).isInstanceOf(TransferCompletedEvent.class);
        TransferCompletedEvent transferCompletedEvent = (TransferCompletedEvent) event;

        assertThat(transferCompletedEvent.getData()).as("Event data is missing").isNotNull();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(transferCompletedEvent.getEventType())
                .as("eventType")
                .isEqualTo("TRANSFER_COMPLETED");
        softly.assertThat(transferCompletedEvent.getAccountId()).as("accountId").isEqualTo(senderId);
        softly.assertThat(transferCompletedEvent.getRelatedAccountId())
                .as("relatedAccountId")
                .isEqualTo(receiverId);
        softly.assertThat(transferCompletedEvent.getData().getAmount()).as("amount").isEqualTo(amount);
        softly.assertThat(transferCompletedEvent.getData().getToAccount())
                .as("toAccount")
                .isEqualTo(receiverId);
        softly.assertThat(transferCompletedEvent.getTimestamp()).as("timestamp").isNotNull();
        softly.assertAll();
    }
}