package kr.sooragenius.shop.review.service;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.service.infra.ItemRepository;
import kr.sooragenius.shop.review.Review;
import kr.sooragenius.shop.review.dto.ReviewDTO;
import kr.sooragenius.shop.review.service.infra.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;

    public Long addReview(ReviewDTO.Request request) {
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        Review of = Review.of(request, item);
        Review save = reviewRepository.save(of);

        return save.getId();
    }
    public ReviewDTO.Response findById(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        return ReviewDTO.Response.of(review);
    }
}
