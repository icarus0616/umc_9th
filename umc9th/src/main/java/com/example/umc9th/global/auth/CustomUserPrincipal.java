package com.example.umc9th.global.auth;

import com.example.umc9th.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserPrincipal implements UserDetails {

    private final Long memberId;
    private final String username;

    public CustomUserPrincipal(Member member) {
        this.memberId = member.getUserId();
        this.username = member.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 필요하면 Role 추가
        return List.of();
    }

    @Override
    public String getPassword() {
        return null; // 소셜 로그인만 쓴다고 가정
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
