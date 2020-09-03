package kr.sooragenius.shop.item.event.impl;

import kr.sooragenius.shop.config.EmbededRedisTestConfiguration;
import kr.sooragenius.shop.order.dto.ItemOrderEventDTO;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Import(EmbededRedisTestConfiguration.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DataRedisTest
class ItemOrderEventListenerTest {
    @Mock
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RedisTemplate redisTemplate;


    private final String REDIS_STOCK_KEY = "item::1::1::stock";
    private ItemOrderEventListener orderEventListener;
    @BeforeEach
    public void setUp() {
        System.out.println("setup");

        orderEventListener = new ItemOrderEventListener(redisTemplate, applicationEventPublisher);

        redisTemplate.delete(REDIS_STOCK_KEY);
    }
    @Test
    @DisplayName("ItemOrderEventListener.itemOrderEvent - 재고 부족")
    void itemOrderEventNotEnoughStock() {
        // given
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(REDIS_STOCK_KEY, "1");

        ItemOrderEventDTO.NewItemOrder build = ItemOrderEventDTO.NewItemOrder.builder().itemId(1L).optionId(2L).stock(1).build();

        // then
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> orderEventListener.itemOrderEvent(build))
                .withMessageContaining("재고가 부족");
    }
    @Test
    @DisplayName("ItemOrderEventListener.itemOrderEvent - 재고 부족")
    void itemOrderEventEnoguhStock() {
        // given
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(REDIS_STOCK_KEY, "1");

        ItemOrderEventDTO.NewItemOrder build = ItemOrderEventDTO.NewItemOrder.builder().itemId(1L).optionId(1L).stock(1).build();

        // when
        orderEventListener.itemOrderEvent(build);

        // then
        assertThat(valueOperations.get(REDIS_STOCK_KEY))
                .isEqualTo("0");
    }

    @Test
    @DisplayName("ItemOrderEventListener.cancelEvent - Redis 재고 데이터 없음")
    void cancelEventNotExistsStockInRedis() {
        // given
        ItemOrderEventDTO.NewItemOrderRollback build = ItemOrderEventDTO.NewItemOrderRollback.builder()
                .itemId(1L)
                .optionId(1L)
                .stock(1L)
                .build();
        // when
        orderEventListener.cancelEvent(build);

        // then
        assertThat(redisTemplate.opsForValue().get(REDIS_STOCK_KEY))
                .isEqualTo(String.valueOf(build.getStock()));
    }

    @Test
    @DisplayName("ItemOrderEventListener.cancelEvent - Redis 기존 재고 존재")
    void cancelEvent() {
        // given
        long stock = 3;

        redisTemplate.opsForValue().set(REDIS_STOCK_KEY, String.valueOf(stock));

        ItemOrderEventDTO.NewItemOrderRollback build = ItemOrderEventDTO.NewItemOrderRollback.builder()
                .itemId(1L)
                .optionId(1L)
                .stock(1L)
                .build();
        // when
        orderEventListener.cancelEvent(build);

        // then
        assertThat(redisTemplate.opsForValue().get(REDIS_STOCK_KEY))
                .isEqualTo(String.valueOf(build.getStock() + stock));
    }
}