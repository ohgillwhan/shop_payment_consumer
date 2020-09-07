package kr.sooragenius.shop.kafka;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import kr.sooragenius.shop.config.KafkConfigurationTest;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(KafkConfigurationTest.class)
public class KafkaProduceTest {
    private final KafkaTemplate kafkaTemplate;


    @Test
    public void prod() throws InterruptedException {
        for(int i = 0; i<1000; i++) {
            kafkaTemplate.send("NEW-Topic", i%5,"Hello"+i, "A"+i);
        }

        Thread.sleep(1000L);
    }

    @KafkaListener(groupId = "Hello" , topics = "NEW-Topic", containerFactory = "kafkaListenerContainerFactory")
    public void listner(String s, ConsumerRecordMetadata meta) {
        System.out.println("Consume : " + meta.partition() + " "+s);
    }
    @KafkaListener(groupId = "Hello2" , topics = "NEW-Topic", containerFactory = "kafkaListenerContainerFactory")
    public void listner2(String s, ConsumerRecordMetadata meta) {
        System.out.println("Consume2 : " + meta.partition() + " "+s);
    }
}
