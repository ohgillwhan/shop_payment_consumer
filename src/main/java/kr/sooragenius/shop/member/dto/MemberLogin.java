package kr.sooragenius.shop.member.dto;

import kr.sooragenius.shop.member.Member;
import kr.sooragenius.shop.member.enums.MemberAuthority;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class MemberLogin implements UserDetails {
    private String id;

    private String password;
    private String name;

    private MemberAuthority authority;


    public static MemberLogin of(Member member) {
        MemberLogin memberLogin = new MemberLogin();

        memberLogin.id = member.getId();
        memberLogin.password = member.getPassword();
        memberLogin.name = member.getName();
        memberLogin.authority = member.getAuthority();

        return memberLogin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(authority.name()));

        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}