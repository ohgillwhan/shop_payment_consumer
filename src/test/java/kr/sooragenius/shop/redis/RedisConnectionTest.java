package kr.sooragenius.shop.redis;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RedisConnectionTest {
    private  final StringRedisTemplate stringRedisTemplate;

    @Test
    public void redisStringTest() {
        String uuid = UUID.randomUUID().toString();
        String key = "Hello" + uuid;
        String value = "hello";

        assertThat(stringRedisTemplate.opsForValue().get(key))
                .isNull();


        stringRedisTemplate.opsForValue().set(key, value);

        assertThat(stringRedisTemplate.opsForValue().get(key))
                .isNotEmpty()
                .isEqualTo(value);

        assertThat(stringRedisTemplate.delete(key))
                .isTrue();
    }
}
