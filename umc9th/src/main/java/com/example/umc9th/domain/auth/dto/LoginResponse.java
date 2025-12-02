package com.example.umc9th.domain.auth.dto;

import com.example.umc9th.domain.kakao.dto.KakaoDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private KakaoDto kakao;
    private String accessToken;
    private String tokenType; // "Bearer"
}
