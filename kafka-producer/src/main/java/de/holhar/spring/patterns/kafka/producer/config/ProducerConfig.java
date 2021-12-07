package de.holhar.spring.patterns.kafka.producer.config;

import de.holhar.spring.patterns.kafka.producer.domain.EventPayload;
import de.holhar.spring.patterns.kafka.producer.domain.EventWrapper;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Import(KafkaConfiguration.class)
public class ProducerConfig {

    @Bean
    public AdminClient kafkaAdminClient(KafkaConfiguration kafkaConfig) {
        Map<String, Object> configProps = getKafkaConfigProperties(kafkaConfig);
        return AdminClient.create(configProps);
    }

    @Bean
    public ProducerFactory<String, EventWrapper<EventPayload>> producerFactory(KafkaConfiguration kafkaConfig) {
        Map<String, Object> configProps = getKafkaConfigProperties(kafkaConfig);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, EventWrapper<EventPayload>> kafkaTemplate(KafkaConfiguration kafkaConfig) {
        return new KafkaTemplate<>(producerFactory(kafkaConfig));
    }

    private Map<String, Object> getKafkaConfigProperties(KafkaConfiguration kafkaConfig) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG, 2);
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        configureSSL(configProps, kafkaConfig);
        return configProps;
    }

    private void configureSSL(Map<String, Object> props, KafkaConfiguration kafkaConfig) {
        if (kafkaConfig.isSslEnabled()) {
            props.put("security.protocol", "SSL");
            props.put("ssl.truststore.location", kafkaConfig.getSslTrustStoreLocation());
            props.put("ssl.truststore.password", kafkaConfig.getSslTrustStorePassword());

            props.put("ssl.key.password", kafkaConfig.getSslKeyStorePassword());
            props.put("ssl.keystore.password", kafkaConfig.getSslKeyStorePassword());
            props.put("ssl.keystore.location", kafkaConfig.getSslKeyStoreLocation());
        }
    }
}
