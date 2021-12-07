package de.holhar.spring.patterns.kafka.producer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.kafka")
@Configuration
public class KafkaConfiguration {

    private String bootstrapServers;
    private String topic;
    private String consumerGroupId;
    private String consumerAutoOffsetReset;
    private String sslKeyStoreLocation;
    private String sslKeyStorePassword;
    private String sslTrustStoreLocation;
    private String sslTrustStorePassword;
    private boolean sslEnabled;

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumerGroupId() {
        return consumerGroupId;
    }

    public void setConsumerGroupId(String consumerGroupId) {
        this.consumerGroupId = consumerGroupId;
    }

    public String getConsumerAutoOffsetReset() {
        return consumerAutoOffsetReset;
    }

    public void setConsumerAutoOffsetReset(String consumerAutoOffsetReset) {
        this.consumerAutoOffsetReset = consumerAutoOffsetReset;
    }

    public String getSslKeyStoreLocation() {
        return sslKeyStoreLocation;
    }

    public void setSslKeyStoreLocation(String sslKeyStoreLocation) {
        this.sslKeyStoreLocation = sslKeyStoreLocation;
    }

    public String getSslKeyStorePassword() {
        return sslKeyStorePassword;
    }

    public void setSslKeyStorePassword(String sslKeyStorePassword) {
        this.sslKeyStorePassword = sslKeyStorePassword;
    }

    public String getSslTrustStoreLocation() {
        return sslTrustStoreLocation;
    }

    public void setSslTrustStoreLocation(String sslTrustStoreLocation) {
        this.sslTrustStoreLocation = sslTrustStoreLocation;
    }

    public String getSslTrustStorePassword() {
        return sslTrustStorePassword;
    }

    public void setSslTrustStorePassword(String sslTrustStorePassword) {
        this.sslTrustStorePassword = sslTrustStorePassword;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }
}
