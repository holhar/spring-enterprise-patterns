package de.holhar.spring.patterns.kafka.producer.monitoring;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * POI: https://github.com/spring-projects/spring-boot/issues/14088
 */
@Component
public class KafkaHealthIndicator implements HealthIndicator {

    private final DescribeClusterResult describeCluster;

    @Autowired
    public KafkaHealthIndicator(AdminClient adminClient) {
        final DescribeClusterOptions describeClusterOptions = new DescribeClusterOptions().timeoutMs(10000);
        this.describeCluster = adminClient.describeCluster(describeClusterOptions);
    }

    @Override
    public Health health() {
        try {
            final String clusterId = describeCluster.clusterId().get();
            final int nodeCount = describeCluster.nodes().get().size();
            return Health.up()
                    .withDetail("clusterId", clusterId)
                    .withDetail("nodeCount", nodeCount)
                    .build();
        } catch (ExecutionException e) {
            return Health.down()
                    .withException(e)
                    .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Health.down()
                    .withException(e)
                    .build();
        }
    }
}