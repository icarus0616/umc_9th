package com.example.umc9th.domain.kakao.controller;

import com.example.umc9th.domain.auth.dto.LoginResponse;
import com.example.umc9th.domain.auth.service.RefreshTokenService;
import com.example.umc9th.domain.kakao.dto.KakaoDto;
import com.example.umc9th.domain.kakao.service.KakaoService;
import com.example.umc9th.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.refresh-token-validity-in-ms}")
    private long refreshTokenValidityInMs;

    @GetMapping("/login/kakao/callback")
    public ResponseEntity<LoginResponse> callback(HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        String code = request.getParameter("code");
        KakaoDto kakaoInfo = kakaoService.login(code); // 여기서 memberId까지 세팅됨

        Long memberId = kakaoInfo.getMemberId();

        // 1) 우리 JWT 발급
        String accessToken = jwtTokenProvider.generateAccessToken(memberId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(memberId);

        // 2) RefreshToken DB 저장
        refreshTokenService.save(memberId, refreshToken, refreshTokenValidityInMs);

        // 3) RefreshToken 을 HttpOnly 쿠키로 내려주기
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)      // HTTPS면 true, 로컬 개발이면 false도 가능
                .path("/")
                .maxAge(refreshTokenValidityInMs / 1000) // 초 단위
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // 4) AccessToken 은 바디로
        LoginResponse body = LoginResponse.builder()
                .kakao(kakaoInfo)
                .accessToken(accessToken)
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok(body);
    }
}
