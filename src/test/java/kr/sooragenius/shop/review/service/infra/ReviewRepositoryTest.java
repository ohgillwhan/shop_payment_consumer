package kr.sooragenius.shop.review.service;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.dto.CategoryDTO;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
import kr.sooragenius.shop.item.service.infra.ItemRepository;
import kr.sooragenius.shop.review.Review;
import kr.sooragenius.shop.review.dto.ReviewDTO;
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
class ReviewServiceTest {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewService reviewService;
    private final EntityManager entityManager;

    @Test
    @Transactional
    void addItem() {
        Long categoryId = addTopCategory();
        Long itemId = addKakaoItem(categoryId);

        ReviewDTO.Request nice = ReviewDTO.Request.builder().itemId(itemId).contents("nice").build();
        ReviewDTO.Request bad = ReviewDTO.Request.builder().itemId(itemId).contents("bad").build();
        ReviewDTO.Request normal = ReviewDTO.Request.builder().itemId(itemId).contents("normal").build();

        Long niceReviewId = reviewService.addReview(nice);
        Long badReviewId = reviewService.addReview(bad);
        Long normalReviewId = reviewService.addReview(normal);

        flush();

        Map<Long, ReviewDTO.Request> itemMaps = new HashMap<>();
        itemMaps.put(niceReviewId, nice);
        itemMaps.put(badReviewId, bad);
        itemMaps.put(normalReviewId, normal);

        itemMaps.entrySet().stream().forEach(item -> {
            Long key = item.getKey();
            ReviewDTO.Request value = item.getValue();

            ReviewDTO.Response byId = reviewService.findById(key);

            assertEquals(key, byId.getId());
            assertEquals(value.getContents(), byId.getContents());
            assertEquals(itemId, byId.getItemId());
        });
    }
    private Long addTopCategory() {
        return categoryRepository.save(Category.of(CategoryDTO.Request.builder().name("TOP").build())).getId();
    }
    private Long addKakaoItem(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).get();

        return itemRepository.save(Item.of(ItemDTO.Request.builder().name("Kakao").build(), category)).getId();
    }
    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}