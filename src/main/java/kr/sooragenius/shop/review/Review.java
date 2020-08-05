package kr.sooragenius.shop.review;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.review.dto.ReviewDTO;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private Long id;
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Item item;

    public static Review of(ReviewDTO.Request request, Item item) {
        Review review = new Review();

        review.item = item;
        review.contents = request.getContents();

        return review;
    }
}
