package org.morski.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.morski.config.TestConfig;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class KafkaConsumerClient implements AutoCloseable {
    private final KafkaConsumer<String, String> consumer;
    private final String topic;

    public KafkaConsumerClient() {
        this(TestConfig.getKafkaAccountEventsTopic());
    }

    public KafkaConsumerClient(String topic) {
        this.topic = topic;
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, TestConfig.getKafkaBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + UUID.randomUUID());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.consumer = new KafkaConsumer<>(props);
        this.consumer.subscribe(Collections.singletonList(topic));
    }

    public ConsumerRecord<String, String> poll(Duration timeout) {
        ConsumerRecords<String, String> records = consumer.poll(timeout);
        if (records.isEmpty()) {
            return null;
        }
        return records.iterator().next();
    }

    @Override
    public void close() {
        if (consumer != null) {
            consumer.close();
        }
    }
}
