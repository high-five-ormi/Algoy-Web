package com.example.algoyweb.util.user;

import com.example.algoyweb.model.entity.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 유저의 역할(Role)을 기반으로 권한을 반환
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getKey()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 이메일을 username으로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        // 유저가 삭제되지 않았으면 계정이 만료되지 않음
        return !user.getIsDeleted();
    }

    @Override
    public boolean isAccountNonLocked() {
        // 필요한 경우, 잠금 로직을 추가 (현재는 항상 true)
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 자격 증명이 만료되지 않았음을 나타냄 (필요 시 수정)
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 유저가 활성화되어 있는지 확인 (삭제된 경우 비활성화)
        return !user.getIsDeleted();
    }

    // 추가 메소드: 닉네임 반환
    public String getNickname() {
        return user.getNickname();
    }
}
