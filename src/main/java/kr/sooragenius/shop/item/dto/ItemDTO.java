package kr.sooragenius.shop.item.dto;

import kr.sooragenius.shop.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ItemDTO {
    @Data
    @NoArgsConstructor @Builder @AllArgsConstructor
    public static class Request {
        private Long categoryId;
        private String name;
        private String contents;
        private String deliveryDescription;
        private Long price;
        private Long discount;
    }
    @Data
    @NoArgsConstructor @Builder @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long categoryId;
        private String name;
        private String contents;
        private String deliveryDescription;
        private Long price;
        private Long discount;
        private Long discountPrice;

        public static Response of(Item item) {
            Response response = new Response();

            response.categoryId = item.getCategory().getId();
            response.id = item.getId();
            response.name = item.getName();
            response.contents = item.getContents();
            response.deliveryDescription = item.getDeliveryDescription();
            response.price = item.getPrice();
            response.discount = item.getDiscount();
            response.discountPrice = item.getDiscountPrice();

            return response;
        }
    }
}
