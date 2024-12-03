package com.mayreh.kafka;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;

public class KafkaAdmin implements AutoCloseable {
    private final Admin adminClient;

    public KafkaAdmin(String bootstrapServers) {
        adminClient = Admin.create(Map.of("bootstrap.servers", bootstrapServers));
    }

    public void createTopic(String topic, int numPartitions, int replicationFactor) {
        get(adminClient.createTopics(List.of(new NewTopic(topic, numPartitions, (short) replicationFactor))).all());
    }

    public void createTopic(String topic, Map<Integer, List<Integer>> assignments) {
        get(adminClient.createTopics(List.of(new NewTopic(topic, assignments))).all());
    }

    public void deleteTopic(String topic) {
        get(adminClient.deleteTopics(List.of(topic)).all());
    }

    @Override
    public void close() {
        adminClient.close();
    }

    private static <T> void get(Future<T> future) {
        try {
            future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
