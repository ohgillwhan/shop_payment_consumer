package kr.sooragenius.shop.member.dto;

import kr.sooragenius.shop.member.Member;
import kr.sooragenius.shop.member.enums.MemberAuthority;
import lombok.Data;

public class MemberDTO {
    @Data
    public static class Request {
        private String id;
        private String password;
        private String name;
        private MemberAuthority authority;
    }
    @Data
    public static class Response {
        private String id;
        private String name;
        private MemberAuthority authority;

        public static Response of(Member member) {
            Response response = new Response();

            response.setId(member.getId());
            response.setName(member.getName());
            response.setAuthority(member.getAuthority());

            return response;
        }
    }
}
