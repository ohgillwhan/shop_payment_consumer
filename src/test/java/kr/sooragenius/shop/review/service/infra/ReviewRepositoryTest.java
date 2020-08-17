package kr.sooragenius.shop.review.service.infra;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.dto.CategoryDTO;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
import kr.sooragenius.shop.item.dto.ItemOptionDTO;
import kr.sooragenius.shop.item.service.infra.ItemRepository;
import kr.sooragenius.shop.review.Review;
import kr.sooragenius.shop.review.dto.ReviewDTO;
import kr.sooragenius.shop.review.enums.ScoreEnums;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReviewRepositoryTest {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("상품의 리뷰 추가 그리고 flush 후 재검증")
    void addReview() {
        //given
        Category category = addTopCategory();
        Item item = addKakaoItem(category);

        ReviewDTO.Request niceRequest = ReviewDTO.Request.builder().contents("nice").score(ScoreEnums.GOOD).build();
        ReviewDTO.Request badRequest = ReviewDTO.Request.builder().contents("bad").score(ScoreEnums.BAD).build();
        ReviewDTO.Request normalRequest = ReviewDTO.Request.builder().contents("normal").score(ScoreEnums.NORMAL).build();

        Review nice = Review.of(niceRequest, item);
        Review bad = Review.of(badRequest, item);
        Review normal = Review.of(normalRequest, item);

        // when
        Long niceReviewId = reviewRepository.save(nice).getId();
        Long badReviewId = reviewRepository.save(bad).getId();
        Long normalReviewId = reviewRepository.save(normal).getId();

        flush();

        // then
        Map<Long, ReviewDTO.Request> itemMaps = new HashMap<>();
        itemMaps.put(niceReviewId, niceRequest);
        itemMaps.put(badReviewId, badRequest);
        itemMaps.put(normalReviewId, normalRequest);

        itemMaps.entrySet().stream().forEach(entry -> {
            Long key = entry.getKey();
            ReviewDTO.Request value = entry.getValue();

            Review byId = reviewRepository.findById(key).get();

            assertThat(byId.getId())
                    .isGreaterThan(0L)
                    .isEqualTo(key);

            assertThat(byId.getContents())
                    .isNotEmpty()
                    .isEqualTo(value.getContents());

            assertThat(byId.getItem().getId())
                    .isNotNull()
                    .isEqualTo(item.getId());

            assertThat(byId.getScore())
                    .isNotNull()
                    .isEqualTo(value.getScore());

        });
    }
    private Category addTopCategory() {
        return categoryRepository.save(Category.of(CategoryDTO.Request.builder().name("TOP").build()));
    }
    private Item addKakaoItem(Category category) {
        return itemRepository.save(Item.of(ItemDTO.Request.builder().name("Kakao").amount(1000L).discountAmount(100L).stock(1L).build(), category));
    }
    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}