package com.example.umc9th.domain.kakao.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoDto {
    private Long id;        // 카카오 유저 ID
    private String nickname;
    private Long memberId;  // 우리 DB member.userId
}
