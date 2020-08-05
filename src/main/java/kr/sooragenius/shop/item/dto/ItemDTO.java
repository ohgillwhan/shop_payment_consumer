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
    }
    @Data
    @NoArgsConstructor @Builder @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long categoryId;
        private String name;

        public static Response of(Item item) {
            Response response = new Response();

            response.setCategoryId(item.getCategory().getId());
            response.setId(item.getId());
            response.setName(item.getName());

            return response;
        }
    }
}
