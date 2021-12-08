package de.holhar.spring.patterns.kafka.consumer.config;

import de.holhar.spring.patterns.kafka.producer.domain.EventPayload;
import de.holhar.spring.patterns.kafka.producer.domain.EventWrapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    KafkaConfiguration kafkaConfig;

    public KafkaConsumerConfig(KafkaConfiguration kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    @Bean
    public ConsumerFactory<String, EventWrapper<EventPayload>> consumerFactory() {
        try (JsonDeserializer<EventWrapper<EventPayload>> valueDeserializer = new JsonDeserializer<>()) {
            valueDeserializer.addTrustedPackages(EventWrapper.class.getPackage().getName());

            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
            configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.getConsumerAutoOffsetReset());
            configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

            configureSSL(configProps, kafkaConfig);

            DefaultKafkaConsumerFactory<String, EventWrapper<EventPayload>> consumerFactory = new DefaultKafkaConsumerFactory<>(configProps);
            consumerFactory.setValueDeserializer(valueDeserializer);
            return consumerFactory;
        }
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventWrapper<EventPayload>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EventWrapper<EventPayload>> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
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
