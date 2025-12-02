package com.example.umc9th.domain.auth.controller;

import com.example.umc9th.domain.auth.dto.LoginResponse;
import com.example.umc9th.domain.auth.service.RefreshTokenService;
import com.example.umc9th.domain.kakao.dto.KakaoDto;
import com.example.umc9th.domain.member.entity.Member;
import com.example.umc9th.domain.member.repository.MemberRepository;
import com.example.umc9th.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;

    @Value("${jwt.refresh-token-validity-in-ms}")
    private long refreshTokenValidityInMs;

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(HttpServletRequest request,
                                                      HttpServletResponse response) {

        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        // 1) JWT 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).build();
        }

        Long memberId = jwtTokenProvider.getMemberIdFromToken(refreshToken);

        // 2) DB에서 refresh token 유효성 체크
        if (!refreshTokenService.isValid(memberId, refreshToken)) {
            return ResponseEntity.status(401).build();
        }

        // 3) 새 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(memberId);

        // (선택) 4) Refresh Token 로테이션 (보안 강화용)
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(memberId);
        refreshTokenService.rotate(memberId, refreshToken, newRefreshToken, refreshTokenValidityInMs);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenValidityInMs / 1000)
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // 5) 응답 바디 (필요하다면 최소 정보만)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        KakaoDto kakaoDto = KakaoDto.builder()
                .memberId(member.getUserId())
                .nickname(member.getName())
                .id(Long.valueOf(member.getSocialId())) // 카카오 ID 담고 싶으면
                .build();

        LoginResponse body = LoginResponse.builder()
                .kakao(kakaoDto)
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok(body);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> "refresh_token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    // ✅ 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            refreshTokenService.revoke(refreshToken);
        }

        // 쿠키 삭제
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // 즉시 만료
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok().build();
    }
}
