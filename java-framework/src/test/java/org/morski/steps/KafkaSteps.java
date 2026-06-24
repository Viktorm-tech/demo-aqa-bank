package org.morski.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.SoftAssertions;
import org.morski.dto.Account;
import org.morski.dto.kafka.AccountCreatedEvent;
import org.morski.dto.kafka.BaseEvent;
import org.morski.kafka.KafkaClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class KafkaSteps {

    private static final Duration BASE_TIMEOUT = Duration.ofSeconds(10);
    private static final String TOPIC = "account-events";
    private final KafkaClient kafkaClient;

    public KafkaSteps(KafkaClient kafkaClient) {
        this.kafkaClient = kafkaClient;
    }

    @Step("Wait for Kafka message for key {key}")
    public BaseEvent waitForEvent(String key, String topic, Duration timeout) {
        ConsumerRecord<String, String> record = kafkaClient.waitForRecordByKey(key, topic, timeout);
        assertThat(record).as("Message with key %s not found", key).isNotNull();
        try {
            return new ObjectMapper().readValue(record.value(), BaseEvent.class);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Failed to deserialize event", e);
        }
    }


    @Step("Verify ACCOUNT_CREATED event")
    public void verifyAccountCreatedEvent(String key, Account account) {
        BaseEvent event = waitForEvent(key, TOPIC, BASE_TIMEOUT);

        assertThat(event).isInstanceOf(AccountCreatedEvent.class);
        AccountCreatedEvent created = (AccountCreatedEvent) event;

        assertThat(created.getData()).as("Event data is missing").isNotNull();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(created.getEventType()).isEqualTo("ACCOUNT_CREATED");
        softly.assertThat(created.getAccountId()).isEqualTo(account.getId());
        softly.assertThat(created.getRelatedAccountId()).isNull();
        softly.assertThat(created.getData().getCustomerId()).isEqualTo(account.getCustomerId());
        softly.assertThat(created.getData().getInitialBalance()).isEqualTo(account.getBalance());
        softly.assertThat(created.getData().getCurrency()).isEqualTo(account.getCurrency());
        softly.assertThat(created.getTimestamp()).isNotNull();
        softly.assertAll();
    }
}
