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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositorySaveTest {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("상품 저장 후 flush 그리고 정상저장 확")
    void addItem() {
        // given
        Category category = addTopCategory();

        ItemDTO.Request itemKakaoRequest = ItemDTO.Request.builder().name("Kakao").amount(1000L).discountAmount(100L).stock(1L).build();
        ItemDTO.Request itemClockRequest = ItemDTO.Request.builder().name("Clock").amount(1000L).discountAmount(100L).stock(1L).build();
        ItemDTO.Request itemPenRequest = ItemDTO.Request.builder().name("Pen").amount(1000L).discountAmount(100L).stock(1L).build();

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

            assertThat(byId.getId())
                    .isGreaterThan(0L)
                    .isEqualTo(key);

            assertThat(byId.getName())
                    .isNotEmpty()
                    .isEqualTo(value.getName());

            assertThat(byId.getCategory().getId())
                    .isGreaterThan(0L)
                    .isEqualTo(category.getId());

            assertThat(byId.getPayAmount())
                    .isEqualTo(900L);

            assertThat(byId.getItemOptions().size())
                    .isEqualTo(1);

            assertThat(byId.getItemOptions().get(0).getPremium())
                    .isEqualTo(0L);

            assertThat(byId.getItemOptions().get(0).getName())
                    .isEqualTo("None");
        });
    }

    private Category addTopCategory() {
        return categoryRepository.save(Category.of(CategoryDTO.Request.builder().name("TOP").build()));
    }
    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}