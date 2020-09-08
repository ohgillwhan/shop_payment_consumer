package kr.sooragenius.shop.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {
    @Bean
    public ProducerFactory producerFactory() {
        Map<String, Object> map = new HashMap<>();

        map.put(ProducerConfig.ACKS_CONFIG, "1");
        map.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        map.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        map.put(ProducerConfig.RETRIES_CONFIG, "3");
        map.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, String.valueOf(1024*1024));


        return new DefaultKafkaProducerFactory(map);
    }
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // props.put(ConsumerConfig.GROUP_ID_CONFIG, "mygroup");
        // smallest
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");    // ENABLE_AUTO_COMMIT_CONFIG가 true일경우 100ms 마다(100ms 이후 새로운 poll이 발생시) offset을 처리된곳 까지 commit을 함
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // earliest
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @KafkaListener(topics = {"ItemOrder"}, groupId = "Test")
    public void listen(String str) {
        System.out.println(str);
    }
}
