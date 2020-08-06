package kr.sooragenius.shop.review;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.review.dto.ReviewDTO;
import kr.sooragenius.shop.review.enums.ScoreEnums;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private Long id;
    private String contents;            // 리뷰
    private String deliveryContents;    // 배송리뷰정보

    @Enumerated(EnumType.STRING)
    private ScoreEnums score;   // 점수
    private int like;           // 좋아요

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Item item;

    public static Review of(ReviewDTO.Request request, Item item) {
        Review review = new Review();

        review.item = item;
        review.score = request.getScore();
        review.contents = request.getContents();
        review.deliveryContents = request.getDeliveryContents();

        return review;
    }
}
