package kr.sooragenius.shop.member;

import kr.sooragenius.shop.member.dto.MemberDTO;
import kr.sooragenius.shop.member.enums.MemberAuthority;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "member_info")
public class Member {
    @Id
    @Column(name = "member_id")
    private String id;

    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private MemberAuthority authority;

    public static Member of(MemberDTO.Request request, PasswordEncoder passwordEncoder) {
        Member member = new Member();

        member.id = request.getId();
        member.password = passwordEncoder.encode(request.getPassword());
        member.name = request.getName();
        member.authority = request.getAuthority();

        return member;
    }
}