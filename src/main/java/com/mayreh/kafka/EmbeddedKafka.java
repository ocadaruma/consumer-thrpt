package com.mayreh.kafka;

import java.util.Properties;

import kafka.testkit.KafkaClusterTestKit;
import kafka.testkit.TestKitNodes;

public class EmbeddedKafka implements AutoCloseable {
    private final KafkaClusterTestKit testKit;

    public EmbeddedKafka(int numBrokers) {
        this(numBrokers, new Properties());
    }

    public EmbeddedKafka(int numBrokers, Properties additionalProperties) {
        KafkaClusterTestKit.Builder builder = new KafkaClusterTestKit.Builder(
                new TestKitNodes.Builder()
                        .setCombined(false)
                        .setNumBrokerNodes(numBrokers)
                        .setNumControllerNodes(1)
                        .build());
        additionalProperties.forEach((k, v) -> builder.setConfigProp((String) k, v));
        try {
            testKit = builder.build();
            testKit.format();
            testKit.startup();
            testKit.waitForReadyBrokers();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String bootstrapServers() {
        return testKit.bootstrapServers();
    }

    @Override
    public void close() {
        try {
            testKit.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
