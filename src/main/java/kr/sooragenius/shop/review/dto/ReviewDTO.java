package kr.sooragenius.shop.review.dto;

import kr.sooragenius.shop.review.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReviewDTO {
    @Data
    @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Request {
        private Long itemId;
        private String contents;
    }

    @Data
    @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Response {
        private Long itemId;
        private Long id;
        private String contents;

        public static Response of(Review review) {
            Response response = new Response();

            response.itemId = review.getItem().getId();
            response.id = review.getId();
            response.contents = review.getContents();

            return response;
        }
    }
}
