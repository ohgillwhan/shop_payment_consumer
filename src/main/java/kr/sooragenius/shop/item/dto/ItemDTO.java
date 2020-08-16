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
        private Long amount;
        private Long discountAmount;
        private Long stock;
    }
    @Data
    @NoArgsConstructor @Builder @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long categoryId;
        private String name;
        private String contents;
        private String deliveryDescription;
        private Long amount;
        private Long discountAmount;
        private Long payAmount;
        private Long stock;

        public static Response of(Item item) {
            Response response = new Response();

            response.categoryId = item.getCategory().getId();
            response.id = item.getId();
            response.name = item.getName();
            response.contents = item.getContents();
            response.deliveryDescription = item.getDeliveryDescription();
            response.amount = item.getAmount();
            response.discountAmount = item.getDiscountAmount();
            response.payAmount = item.getPayAmount();
            response.stock = item.getStock();

            return response;
        }
    }

    @Data
    @NoArgsConstructor @Builder @AllArgsConstructor
    public static class StockUpdate {
        private Long id;
        private Long addStock;
    }
}
