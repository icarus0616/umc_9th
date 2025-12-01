package com.example.umc9th.domain.kakao.service;

import com.example.umc9th.domain.kakao.dto.KakaoDto;
import com.example.umc9th.domain.member.entity.Member;
import com.example.umc9th.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final MemberService memberService;   // 🔹 이걸로 교체

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.client.secret}")
    private String kakaoClientSecret;

    @Value("${kakao.redirect.url}")
    private String kakaoRedirectUrl;

    private static final String KAKAO_AUTH_URI = "https://kauth.kakao.com";
    private static final String KAKAO_API_URI  = "https://kapi.kakao.com";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 로그인 URL
    public String getKakaoLoginUrl() {
        return KAKAO_AUTH_URI + "/oauth/authorize"
                + "?client_id=" + kakaoClientId
                + "&redirect_uri=" + kakaoRedirectUrl
                + "&response_type=code";
    }

    // 인가 코드로 전체 로그인 처리
    public KakaoDto login(String code) throws Exception {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Failed to get authorization code");
        }

        String accessToken = getAccessToken(code);
        KakaoProfile profile = getKakaoProfile(accessToken);

        // 🔹 여기서 우리 서비스 Member로 연결
        Member member = memberService.loginOrCreateKakaoMember(profile.getId(), profile.getNickname());

        return KakaoDto.builder()
                .id(profile.getId())              // 카카오 ID
                .nickname(profile.getNickname())
                .memberId(member.getUserId())     // 우리 서비스 회원 PK
                .build();
    }

    private String getAccessToken(String code) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("client_secret", kakaoClientSecret);
        params.add("redirect_uri", kakaoRedirectUrl);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_AUTH_URI + "/oauth/token",
                HttpMethod.POST,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get Kakao token");
        }

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    private KakaoProfile getKakaoProfile(String accessToken) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_API_URI + "/v2/user/me",
                HttpMethod.POST,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get Kakao user info");
        }

        JsonNode root    = objectMapper.readTree(response.getBody());
        long id          = root.get("id").asLong();
        JsonNode account = root.get("kakao_account");
        JsonNode profile = account.get("profile");

        String nickname  = profile.get("nickname").asText();

        return new KakaoProfile(id, nickname);
    }

    @Getter
    @AllArgsConstructor
    private static class KakaoProfile {
        private final long id;
        private final String nickname;
    }
}
