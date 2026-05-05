package org.morski.steps;

import io.qameta.allure.Step;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.morski.kafka.KafkaClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class KafkaSteps {

    private final KafkaClient kafkaClient;

    public KafkaSteps(KafkaClient kafkaClient) {
        this.kafkaClient = kafkaClient;
    }

    @Step("Wait for Kafka message for key {key}")
    public ConsumerRecord<String, String> waitForEvent(String key, String topic, Duration timeout) {
        ConsumerRecord<String, String> record = kafkaClient.waitForRecordByKey(key, topic, timeout);
        assertThat(record).as("Message with key %s not found", key).isNotNull();
        return record;
    }
}
