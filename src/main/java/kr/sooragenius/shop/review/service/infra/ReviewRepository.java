package kr.sooragenius.shop.review.service.infra;

import kr.sooragenius.shop.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
