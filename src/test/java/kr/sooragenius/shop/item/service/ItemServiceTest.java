package kr.sooragenius.shop.item.service;

import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.dto.CategoryDTO;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
import lombok.RequiredArgsConstructor;
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
class ItemServiceTest {
    private final CategoryRepository categoryRepository;
    private final ItemService itemService;
    private final EntityManager entityManager;

    @Test
    @Transactional
    void addItem() {
        Long categoryId = addTopCategory();

        ItemDTO.Request itemKakao = ItemDTO.Request.builder().categoryId(categoryId).name("Kakao").build();
        ItemDTO.Request itemClock = ItemDTO.Request.builder().categoryId(categoryId).name("Clock").build();
        ItemDTO.Request itemPen = ItemDTO.Request.builder().categoryId(categoryId).name("Pen").build();

        Long kakaoId = itemService.addItem(itemKakao);
        Long clockId = itemService.addItem(itemClock);
        Long penId = itemService.addItem(itemPen);

        flush();

        Map<Long, ItemDTO.Request> itemMaps = new HashMap<>();
        itemMaps.put(kakaoId, itemKakao);
        itemMaps.put(clockId, itemClock);
        itemMaps.put(penId, itemPen);

        itemMaps.entrySet().stream().forEach(item -> {
            Long key = item.getKey();
            ItemDTO.Request value = item.getValue();

            ItemDTO.Response byId = itemService.findById(key);

            assertEquals(key, byId.getId());
            assertEquals(value.getName(), byId.getName());
            assertEquals(categoryId, byId.getCategoryId());
        });
    }
    private Long addTopCategory() {
        return categoryRepository.save(Category.of(CategoryDTO.Request.builder().name("TOP").build())).getId();
    }
    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}