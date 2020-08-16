package kr.sooragenius.shop.item.service.infra;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.dto.CategoryDTO;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
import kr.sooragenius.shop.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("상품 저장")
    void addItem() {
        // given
        Category category = addTopCategory();

        ItemDTO.Request itemKakaoRequest = ItemDTO.Request.builder().name("Kakao").amount(1000L).discountAmount(100L).build();
        ItemDTO.Request itemClockRequest = ItemDTO.Request.builder().name("Clock").amount(1000L).discountAmount(100L).build();
        ItemDTO.Request itemPenRequest = ItemDTO.Request.builder().name("Pen").amount(1000L).discountAmount(100L).build();

        Item itemKakao = Item.of(itemKakaoRequest, category);
        Item itemClock = Item.of(itemClockRequest, category);
        Item itemPen = Item.of(itemPenRequest, category);

        // when
        Long kakaoId = itemRepository.save(itemKakao).getId();
        Long clockId = itemRepository.save(itemClock).getId();
        Long penId = itemRepository.save(itemPen).getId();

        flush();

        // then
        Map<Long, ItemDTO.Request> itemMaps = new HashMap<>();
        itemMaps.put(kakaoId, itemKakaoRequest);
        itemMaps.put(clockId, itemClockRequest);
        itemMaps.put(penId, itemPenRequest);

        itemMaps.entrySet().stream().forEach(item -> {
            Long key = item.getKey();
            ItemDTO.Request value = item.getValue();

            Item byId = itemRepository.findById(key).get();

            assertEquals(key, byId.getId());
            assertEquals(value.getName(), byId.getName());
            assertEquals(category.getId(), byId.getCategory().getId());
            assertEquals(900L, byId.getPayAmount());

            assertEquals(1, byId.getItemOptions().size());
            assertEquals(0L, byId.getItemOptions().get(0).getPremium());
            assertEquals("None", byId.getItemOptions().get(0).getName());
        });
    }
    @Test
    @Transactional
    @DisplayName("재고 부족시 에러")
    public void errorMinusStockByIdWithLock() {
        // given
        Category category = addTopCategory();

        ItemDTO.Request itemKakaoRequest = ItemDTO.Request.builder().name("Kakao").amount(1000L).discountAmount(100L).stock(1L).build();
        Item itemKakao = Item.of(itemKakaoRequest, category);

        Long kakaoId = itemRepository.save(itemKakao).getId();
        flush();
        // when
        long l = itemRepository.minusStockByIdWithLock(kakaoId, 2L);

        // then

        assertTrue(l == 0);
    }

    @Test
    @Transactional
    @DisplayName("재고 충분할경우")
    public void minusStockByIdWithLock() {
        // given
        Category category = addTopCategory();

        ItemDTO.Request itemKakaoRequest = ItemDTO.Request.builder().name("Kakao").amount(1000L).discountAmount(100L).stock(2L).build();
        Item itemKakao = Item.of(itemKakaoRequest, category);

        Long kakaoId = itemRepository.save(itemKakao).getId();
        flush();
        // when
        long l = itemRepository.minusStockByIdWithLock(kakaoId, 2L);

        // then
        assertTrue(l == 1);
    }

    private Category addTopCategory() {
        return categoryRepository.save(Category.of(CategoryDTO.Request.builder().name("TOP").build()));
    }
    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}