package kr.sooragenius.shop.category.dto;

import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.member.dto.MemberLogin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CategoryDTO {

    @Data
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        private Long parentId;
        private String name;
    }
    @Data
    public static class Response {
        private Long id;
        private Long parentId;
        private String name;

        public static Response of(Category category) {
            Response response = new Response();

            response.id = category.getId();
            response.parentId = category.getParent().getId();
            response.name = category.getName();

            return response;
        }
    }
}
