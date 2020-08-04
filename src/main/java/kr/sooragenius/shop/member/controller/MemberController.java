package kr.sooragenius.shop.member.controller;

import kr.sooragenius.shop.member.dto.MemberDTO;
import kr.sooragenius.shop.member.enums.MemberAuthority;
import kr.sooragenius.shop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.security.PermitAll;

@Controller
@RequestMapping("/member")
@PermitAll
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    @GetMapping("/createView")
    public String createView() {
        return "/member/createView";
    }
    @PostMapping(value = {"/", ""})
    public String create(@ModelAttribute MemberDTO.Request request) {
        request.setAuthority(MemberAuthority.ROLE_ADMIN);
        MemberDTO.Response response = memberService.saveMember(request, passwordEncoder);

        return "";
    }
}