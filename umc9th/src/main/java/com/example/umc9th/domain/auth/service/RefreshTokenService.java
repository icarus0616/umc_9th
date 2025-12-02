package com.example.umc9th.domain.auth.service;

import com.example.umc9th.domain.auth.entity.RefreshToken;
import com.example.umc9th.domain.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void save(Long memberId, String token, long refreshTokenValidityInMs) {
        LocalDateTime expiry = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(System.currentTimeMillis() + refreshTokenValidityInMs),
                ZoneId.systemDefault()
        );

        RefreshToken refreshToken = RefreshToken.builder()
                .memberId(memberId)
                .token(token)
                .expiryDate(expiry)
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    public boolean isValid(Long memberId, String token) {
        return refreshTokenRepository
                .findByMemberIdAndTokenAndRevokedFalse(memberId, token)
                .filter(rt -> rt.getExpiryDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public void revoke(String token) {
        refreshTokenRepository.findAll().stream()
                .filter(rt -> rt.getToken().equals(token))
                .forEach(rt -> {
                    rt = RefreshToken.builder()
                            .id(rt.getId())
                            .memberId(rt.getMemberId())
                            .token(rt.getToken())
                            .expiryDate(rt.getExpiryDate())
                            .revoked(true)
                            .build();
                    refreshTokenRepository.save(rt);
                });
    }

    public void rotate(Long memberId, String oldToken, String newToken, long refreshTokenValidityInMs) {
        // 기존 토큰 revoke
        revoke(oldToken);
        // 새 토큰 저장
        save(memberId, newToken, refreshTokenValidityInMs);
    }
}
