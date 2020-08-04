package kr.sooragenius.shop.member.service;


import kr.sooragenius.shop.member.Member;
import kr.sooragenius.shop.member.dto.MemberDTO;
import kr.sooragenius.shop.member.dto.MemberLogin;
import kr.sooragenius.shop.member.service.infra.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberDTO.Response saveMember(MemberDTO.Request request, PasswordEncoder passwordEncoder) {
        Member member = Member.of(request, passwordEncoder);

        Member save = memberRepository.save(member);

        return MemberDTO.Response.of(save);
    }
    public MemberLogin findByIdForLogin(String s) {
        Member member = memberRepository.findById(s).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 계정입니다."));

        return MemberLogin.of(member);
    }
}