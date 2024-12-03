package com.mayreh.kafka;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class Main {
    public static void main(String[] args) {
        try (EmbeddedKafka kafka = new EmbeddedKafka(4);
             KafkaAdmin admin = new KafkaAdmin(kafka.bootstrapServers())) {
            String topic = "test-topic";
            String topic2 = "test-topic2";

            admin.createTopic(topic, Map.of(0, List.of(0, 1, 2)));
            admin.createTopic(topic2, Map.of(0, List.of(0, 1, 2)));

            try (Producer<String, String> producer = new KafkaProducer<>(new Properties() {{
                setProperty("bootstrap.servers", kafka.bootstrapServers());
                setProperty("acks", "all");
                setProperty("max.in.flight.requests.per.connection", "1");
            }}, new StringSerializer(), new StringSerializer());
                 Consumer<String, String> consumer = new KafkaConsumer<>(new Properties() {{
                     setProperty("bootstrap.servers", kafka.bootstrapServers());
                     setProperty("group.id", "test-group");
                     setProperty("enable.auto.commit", "false");
                     setProperty("auto.offset.reset", "earliest");
//                     setProperty("max.poll.records", "100000");
                 }}, new StringDeserializer(), new StringDeserializer())) {
                int numRecords = 1000000;

                for (int i = 0; i < numRecords; i++) {
                    producer.send(new ProducerRecord<>(topic, "test-value-" + i));
                }
                producer.flush();

                consumer.subscribe(List.of(topic, topic2));
                long t0 = System.currentTimeMillis();
                outer: while (true) {
                    for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(100))) {
                        if (record.value().equals("test-value-" + (numRecords - 1))) {
                            break outer;
                        }
                    }
                }
                long elapsed = System.currentTimeMillis() - t0;
                System.out.println("Elapsed: " + elapsed + " ms");
                System.out.println("Throughput: " + ((double) numRecords * 1000 / elapsed) + " records/sec");
            }
        }
    }
}
