package org.morski.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

public class KafkaClient {

    private final KafkaConsumer<String, String> consumer;

    public KafkaClient(KafkaConsumer<String, String> consumer) {
        this.consumer = consumer;
    }

    public ConsumerRecord<String, String> waitForRecordByKey(String expectedKey, String topic, Duration timeout) {
        AtomicReference<ConsumerRecord<String, String>> foundRecord = new AtomicReference<>();

        Awaitility.await()
                .atMost(timeout)
                .pollInterval(Duration.ofMillis(500))
                .until(() -> {
                    var records = consumer.poll(Duration.ofMillis(500));
                    for (ConsumerRecord<String, String> record : records) {
                        if (expectedKey.equals(record.key()) && topic.equals(record.topic())) {
                            foundRecord.set(record);
                            return true;
                        }
                    }
                    return false;
                });

        return foundRecord.get();
    }
}
